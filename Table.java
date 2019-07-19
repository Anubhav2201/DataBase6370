package p4;
/****************************************************************************************
 * @file  Table.java
 *
 *@author   John Miller, Austin Apt, Mingkun Tao, Anubhav Nigam
 */

import static java.lang.System.out;

import java.util.stream.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/****************************************************************************************
 * This class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus and join. The insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
       implements Serializable
{
    /** Relative path for storage directory
     */
    private static final String DIR = "store" + File.separator;

    /** Filename extension for database files
     */
    private static final String EXT = ".dbf";

    /** Counter for naming temporary tables.
     */
    private static int count = 0;

    /** Table name.
     */
    private final String name;

    /** Array of attribute names.
     */
    private final String [] attribute;

    /** Array of attribute domains: a domain may be
     *  integer types: Long, Integer, Short, Byte
     *  real types: Double, Float
     *  string types: Character, String
     */
    private final Class [] domain;

    /** Collection of tuples (data storage).
     */
    private final List <Comparable []> tuples;

    /** Primary key. 
     */
    private final String [] key;

    /** Index into tuples (maps key to tuple number).
     */
    private final Map <KeyType, Comparable []> index;
    
    private final Map <KeyType, Comparable []> bpIndex;
    
    private final Map <KeyType, Comparable []> linIndex;
    
    //----------------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     */  
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = new ArrayList <> ();
        index     = new TreeMap<>();  
        bpIndex   = new BpTreeMap<>(KeyType.class, Comparable [].class);     
        linIndex  = new LinHashMap<>(KeyType.class, Comparable [].class);

    } // constructor

    /************************************************************************************
     * Construct a table from the meta-data specifications and data in _tuples list.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     * @param _tuples      the list of tuples containing the data
     */  
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key,
                  List <Comparable []> _tuples)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = _tuples;
        index     =  new TreeMap<>();
        bpIndex   = new BpTreeMap<>(KeyType.class, Comparable [].class);       // also try BPTreeMap, LinHashMap or ExtHashMap
        linIndex  = new LinHashMap<>(KeyType.class, Comparable [].class);
    } // constructor

    /************************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param name        the name of the relation
     * @param attributes  the string containing attributes names
     * @param domains     the string containing attribute domains (data types)
     */
    public Table (String name, String attributes, String domains, String _key)
    {
        this (name, attributes.split (" "), findClass (domains.split (" ")), _key.split(" "));

        out.println ("DDL> create table " + name + " (" + attributes + ")");
    } // constructor

    //----------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given attributes.
     * Check whether the original key is included in the projection.
     *
     * #usage movie.project ("title year studioNo")
     *
     * @author Austin Apt
     * @param attributes  the attributes to project onto
     * @return  a table of projected tuples
     */
    public Table project (String attributes)
    {
        out.println ("RA> " + name + ".project (" + attributes + ")");
        String [] attrs     = attributes.split (" ");
        Class []  colDomain = extractDom (match (attrs), domain);
        String [] newKey    = (Arrays.asList (attrs).containsAll (Arrays.asList (key))) ? key : attrs;

        List <Comparable []> rows = new ArrayList <> ();

        List <Comparable []> subset = new ArrayList <> ();
        int len = tuples.size();

        //if (Arrays.asList (newKey).containsAll (Arrays.asList (key)))
        for (int i = 0; i < len; i++)
            subset.add(extract((tuples.get(i)), attrs));
        //now remove duplicates
        for (int j=0; j<len; j++) {
            for (int k=j+1; k<len+1; k++) {
                if(k==len)
                    rows.add(subset.get(j));
                else if((subset.get(j)).equals(subset.get(k)))
                    break;
            }
        }
        return new Table (name + count++, attrs, colDomain, newKey, rows);
    } // project

    /************************************************************************************
     * Select the tuples satisfying the given predicate (Boolean function).
     *
     * #usage movie.select (t -> t[movie.col("year")].equals (1977))
     *
     * @author Austin Apt
     * @param predicate  the check condition for tuples
     * @return  a table with tuples satisfying the predicate
     */
    public Table select (Predicate <Comparable []> predicate)
    {
        out.println ("RA> " + name + ".select (" + predicate + ")");

        return new Table (name + count++, attribute, domain, key,
                   tuples.stream ().filter (t -> predicate.test (t))
                                   .collect (Collectors.toList ()));
    } // select

    /************************************************************************************
     * Select the tuples satisfying the given key predicate (key = value).  Use an index
     * (Map) to retrieve the tuple with the given key value.
     *
     * @param keyVal  the given key value
     * @return  a table with the tuple satisfying the key predicate
     */
    public Table select (KeyType keyVal)
    {
        out.println ("RA> " + name + ".select (" + keyVal + ")");

        List <Comparable []> rows = new ArrayList <> ();

        //rows.add(bpIndex.get(keyVal));
        rows.add(linIndex.get(keyVal));

        return new Table (name + count++, attribute, domain, key, rows);
        
    } // select

    /************************************************************************************
     * Union this table and table.  Check that the two tables are compatible.
     *
     * #usage movie.union (show)
     *
     * @author Mingkun Tao
     * @param table  the rhs table in the union operation
     * @return  a table representing the union
     */
    public Table union (Table table)
    {
        out.println ("RA> " + name + ".union (" + table.name + ")");
        if (! compatible (table)) return null;

        List <Comparable []> rows = new ArrayList <> ();

        rows.addAll(this.tuples);
        List <Comparable []> t2 = table.tuples;
        for (Comparable[] comparables : t2) {
        	boolean hasSameTag = false;
        	KeyType t2KeyType = new KeyType(comparables);
			for (Comparable[] row : rows) {
				KeyType rowKeyType = new KeyType(row);
				if (t2KeyType.compareTo(rowKeyType) == 0) {
					hasSameTag = true;
				}
			}
			if (!hasSameTag) {
				rows.add(comparables);
			}
		}

        return new Table (name + count++, attribute, domain, key, rows);
    } // union

    /************************************************************************************
     * Take the difference of this table and table.  Check that the two tables are
     * compatible.
     *
     * #usage movie.minus (show)
     *
     * @author Mingkun Tao
     * @param table  The rhs table in the minus operation
     * @return  a table representing the difference
     */
    public Table minus (Table table)
    {
        out.println ("RA> " + name + ".minus (" + table.name + ")");
        if (! compatible (table)) return null;

        List <Comparable []> rows = new ArrayList <> ();

        rows.addAll(this.tuples);
        List <Comparable []> t2 = table.tuples;
        List <Comparable []> temp = new ArrayList <> ();
        for (Comparable[] row : rows) {
        	boolean hasSameTag = false;
			KeyType rowKeyType = new KeyType(row);
			for (Comparable[] comparables : t2) {
				KeyType t2KeyType = new KeyType(comparables);
				if (rowKeyType.compareTo(t2KeyType) == 0) {
					hasSameTag = true;
				}
			}
			if (hasSameTag) {
				temp.add(row);
			}
		}
        
        rows.removeAll(temp);
        
        return new Table (name + count++, attribute, domain, key, rows);
    } // minus

    /************************************************************************************
     * Join this table and table by performing an "equi-join".  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by append "2" to the end of any duplicate attribute name.
     *
     * #usage movie.join ("studioNo", "name", studio)
     *
     * @author Anubhav Nigam
     *
     * @param attribute1  the attributes of this table to be compared (Foreign Key)
     * @param attribute2  the attributes of table to be compared (Primary Key)
     * @param table      the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (String attributes1, String attributes2, Table table)
    {
        out.println ("RA> " + name + ".join (" + attributes1 + ", " + attributes2 + ", "
                + table.name + ")");

        String [] t_attrs = attributes1.split (" ");
        String [] u_attrs = attributes2.split (" ");

        List <Comparable []> rows = new ArrayList <> ();

        int[] matchedColIndex1 = match(t_attrs);
        int[] matchedColIndex2 = match(u_attrs);

        tuples.forEach(tab1->rows.addAll(table.tuples.stream().filter(tab2->areEqual(tab1,matchedColIndex1,tab2,matchedColIndex2)).map(_row->concat(tab1,_row)).collect(Collectors.toList())));


        return new Table (name + count++, ArrayUtil.concat (attribute, table.attribute),
                ArrayUtil.concat (domain, table.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table by performing an "natural join".  Tuples from both tables
     * are compared requiring common attributes to be equal.  The duplicate column is also
     * eliminated.
     *
     * #usage movieStar.join (starsIn)
     *
     * @author Anubhav Nigam
     *
     * @param table  the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (Table table)
    {
        out.println ("RA> " + name + ".join (" + table.name + ")");

        out.println ("RA> " + name + ".join (" + table.name + ")");

        List <Comparable []> rows = new ArrayList <> ();
        List <Comparable []> rowsIntermediate = new ArrayList <> ();
        // finding out the common attribute from both tables
        List<String> matchedAttribute = Arrays.asList(attribute).stream().filter(a->Arrays.asList(table.attribute).contains(a)).collect(Collectors.toList());
        // finding out uncommon attribute from Table1
        List<String> unMatchedAttributeTable1 = Arrays.asList(attribute).stream().filter(a->!Arrays.asList(table.attribute).contains(a)).collect(Collectors.toList());
        //finding out uncommon attribute in table
        List<String> unMatchedAttributetable = Arrays.asList(table.attribute).stream().filter(a->!Arrays.asList(attribute).contains(a)).collect(Collectors.toList());
        int length = (matchedAttribute.size()+unMatchedAttributeTable1.size()+unMatchedAttributetable.size());
        //Concatenating all three attribute list and storing them in array of String
        String[] finalAttributes = concatenateLists(matchedAttribute,unMatchedAttributeTable1,unMatchedAttributetable).toArray(new String[length]);
        //match attribute and domain to return index of matched column
        int[] matchedColIndex1 = match(matchedAttribute.toArray(new String[matchedAttribute.size()]));
        int[] matchedColIndex2 = table.match(matchedAttribute.toArray(new String[matchedAttribute.size()]));
        //adding all the filtered tuples in list which have all the attribute from both the table
        tuples.forEach(tab1->rowsIntermediate.addAll(table.tuples.stream().filter(tab2->areEqual(tab1,matchedColIndex1,tab2,matchedColIndex2)).map(_row->concat(tab1,_row)).collect(Collectors.toList())));
        //eliminating attributes that are duplicate
        rows=rowsIntermediate.stream().map(row->extract(row, finalAttributes)).collect(Collectors.toList());
        return new Table (name + count++, finalAttributes,
                ArrayUtil.concat (domain, table.domain), key, rows);
    } // join

    /************************************************************************************
     * Return the column position for the given attribute name.
     *
     * @param attr  the given attribute name
     * @return  a column position
     */

    /************************************************************************************
     * Compares tuples from both the table on the basis of matched column index
     * and check for equality
     *
     * @author Anubhav Nigam
     *
     * #usage areEqual (starsIn.tuple, matchedColIndex1, movieStar.tuple, matchedColIndex2)
     *
     * @param table1 , matched column index in table1, table , matched column index in table
     * @return  boolean on the basis of equality check of tuples
     */
    private boolean areEqual(Comparable[] tab1,int[] matchedColIndex1,Comparable[] tab2,int[] matchedColIndex2){
        for(int i=0;i<matchedColIndex1.length;i++){
            if(!tab1[matchedColIndex1[i]].equals(tab2[matchedColIndex2[i]])){
                return false;
            }
        }
        return true;
    }

    /************************************************************************************
     * concat and map two comparable
     *
     *
     *
     * #usage concat (table1.tuple, table.tuple)
     *
     * @author Anubhav Nigam
     * @param list1, list2
     * @return comparable list
     */
    private Comparable[] concat(Comparable[] tuple,Comparable[] row){
        return ArrayUtil.concat(tuple, row);
    }

    /************************************************************************************
     * Concatenate multiple lists
     *
     *
     *
     * #usage concatenateLists (starsIn, movieStar, movie)
     *
     * @param list1, list2, list3
     * @return concatenated list
     */

    public static<T> List<T> concatenateLists(List<T>... lists)
    {
        return Stream.of(lists)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
    }


    public int col (String attr)
    {
        for (int i = 0; i < attribute.length; i++) {
           if (attr.equals (attribute [i])) return i;
        } // for

        return -1;  // not found
    } // col

    /************************************************************************************
     * Insert a tuple to the table.
     *
     * #usage movie.insert ("'Star_Wars'", 1977, 124, "T", "Fox", 12345)
     *
     * @param tup  the array of attribute values forming the tuple
     * @return  whether insertion was successful
     */
    public boolean insert (Comparable [] tup)
    {
        out.println ("DML> insert into " + name + " values ( " + Arrays.toString (tup) + " )");

        if (typeCheck (tup)) {
            tuples.add (tup);
            Comparable [] keyVal = new Comparable [key.length];
            int []        cols   = match (key);
            for (int j = 0; j < keyVal.length; j++) keyVal [j] = tup [cols [j]];
            index.put (new KeyType (keyVal), tup);
            bpIndex.put (new KeyType (keyVal), tup);
            linIndex.put(new KeyType (keyVal), tup);
            return true;
        } else {
            return false;
        } // if
    } // insert

    /************************************************************************************
     * Get the name of the table.
     *
     * @return  the table's name
     */
    public String getName ()
    {
        return name;
    } // getName

    /************************************************************************************
     * Print this table.
     */
    public void print ()
    {
        out.println ("\n Table " + name);
        out.print ("|-");
        for (int i = 0; i < attribute.length; i++) out.print ("---------------");
        out.println ("-|");
        out.print ("| ");
        for (String a : attribute) out.printf ("%15s", a);
        out.println (" |");
        out.print ("|-");
        for (int i = 0; i < attribute.length; i++) out.print ("---------------");
        out.println ("-|");
        for (Comparable [] tup : tuples) {
            out.print ("| ");
            for (Comparable attr : tup) out.printf ("%15s", attr);
            out.println (" |");
        } // for
        out.print ("|-");
        for (int i = 0; i < attribute.length; i++) out.print ("---------------");
        out.println ("-|");
    } // print

    /************************************************************************************
     * Print this table's index (Map).
     */
    public void printIndex ()
    {
        out.println ("\n Index for " + name);
        out.println ("-------------------");
        for (Map.Entry <KeyType, Comparable []> e : index.entrySet ()) {
            out.println (e.getKey () + " -> " + Arrays.toString (e.getValue ()));
        } // for
        out.println ("-------------------");
    } // printIndex

    /************************************************************************************
     * Load the table with the given name into memory.
     *
     * @param name  the name of the table to load
     */
    public static Table load (String name)
    {
        Table tab = null;
        try {
            ObjectInputStream ois = new ObjectInputStream (new FileInputStream (DIR + name + EXT));
            tab = (Table) ois.readObject ();
            ois.close ();
        } catch (IOException ex) {
            out.println ("load: IO Exception");
            ex.printStackTrace ();
        } catch (ClassNotFoundException ex) {
            out.println ("load: Class Not Found Exception");
            ex.printStackTrace ();
        } // try
        return tab;
    } // load

    /************************************************************************************
     * Save this table in a file.
     */
    public void save ()
    {
        try {
            ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream (DIR + name + EXT));
            oos.writeObject (this);
            oos.close ();
        } catch (IOException ex) {
            out.println ("save: IO Exception");
            ex.printStackTrace ();
        } // try
    } // save

    //----------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Determine whether the two tables (this and table) are compatible, i.e., have
     * the same number of attributes each with the same corresponding domain.
     *
     * @param table  the rhs table
     * @return  whether the two tables are compatible
     */
    private boolean compatible (Table table)
    {
        if (domain.length != table.domain.length) {
            out.println ("compatible ERROR: table have different arity");
            return false;
        } // if
        for (int j = 0; j < domain.length; j++) {
            if (domain [j] != table.domain [j]) {
                out.println ("compatible ERROR: tables disagree on domain " + j);
                return false;
            } // if
        } // for
        return true;
    } // compatible

    /************************************************************************************
     * Match the column and attribute names to determine the domains.
     *
     * @param column  the array of column names
     * @return  an array of column index positions
     */
    private int [] match (String [] column)
    {
        int [] colPos = new int [column.length];

        for (int j = 0; j < column.length; j++) {
            boolean matched = false;
            for (int k = 0; k < attribute.length; k++) {
                if (column [j].equals (attribute [k])) {
                    matched = true;
                    colPos [j] = k;
                } // for
            } // for
            if ( ! matched) {
                out.println ("match: domain not found for " + column [j]);
            } // if
        } // for

        return colPos;
    } // match

    /************************************************************************************
     * Extract the attributes specified by the column array from tuple t.
     *
     * @param t       the tuple to extract from
     * @param column  the array of column names
     * @return  a smaller tuple extracted from tuple t
     */
    private Comparable [] extract (Comparable [] t, String [] column)
    {
        Comparable [] tup = new Comparable [column.length];
        int [] colPos = match (column);
        for (int j = 0; j < column.length; j++) tup [j] = t [colPos [j]];
        return tup;
    } // extract

    /************************************************************************************
     * Check the size of the tuple (number of elements in list) as well as the type of
     * each value to ensure it is from the right domain.
     *
     * @param t  the tuple as a list of attribute values
     * @return  whether the tuple has the right size and values that comply
     *          with the given domains
     */
    private boolean typeCheck (Comparable [] t)
    {
        //  T O   B E   I M P L E M E N T E D

        return true;
    } // typeCheck

    /************************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className  the array of class name (e.g., {"Integer", "String"})
     * @return  an array of Java classes
     */
    private static Class [] findClass (String [] className)
    {
        Class [] classArray = new Class [className.length];

        for (int i = 0; i < className.length; i++) {
            try {
                classArray [i] = Class.forName ("java.lang." + className [i]);
            } catch (ClassNotFoundException ex) {
                out.println ("findClass: " + ex);
            } // try
        } // for

        return classArray;
    } // findClass

    /************************************************************************************
     * Extract the corresponding domains.
     *
     * @param colPos the column positions to extract.
     * @param group  where to extract from
     * @return  the extracted domains
     */
    private Class [] extractDom (int [] colPos, Class [] group)
    {
        Class [] obj = new Class [colPos.length];

        for (int j = 0; j < colPos.length; j++) {
            obj [j] = group [colPos [j]];
        } // for

        return obj;
    } // extractDom

} // Table class

