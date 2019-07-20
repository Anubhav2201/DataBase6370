package project3;
/*****************************************************************************************
 * @file  MovieDB.java
 *
 * @author   John Miller
 */

import static java.lang.System.out;

import java.util.ArrayList;

/*****************************************************************************************
 * The MovieDB class makes a Movie Database.  It serves as a template for making other
 * databases.  See "Database Systems: The Complete Book", second edition, page 26 for more
 * information on the Movie Database schema.
 */
class MovieDB
{
    /*************************************************************************************
     * Main method for creating, populating and querying a Movie Database.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        out.println ();

        Table movie = new Table ("movie", "title year length genre studioName producerNo",
                                          "String Integer Integer String String Integer", "title year");

        Table cinema = new Table ("cinema", "title year length genre studioName producerNo",
                                            "String Integer Integer String String Integer", "title year");

        Table movieStar = new Table ("movieStar", "name address gender birthdate",
                                                  "String String Character String", "name");

        Table starsIn = new Table ("starsIn", "movieTitle movieYear starName",
                                              "String Integer String", "movieTitle movieYear starName");

        Table movieExec = new Table ("movieExec", "certNo name address fee",
                                                  "Integer String String Float", "certNo");

        Table studio = new Table ("studio", "name address presNo",
                                            "String String Integer", "name");
        

        TupleGeneratorImpl test = new TupleGeneratorImpl ();
        
       
        
        test.addRelSchema ("movieStar", "name address gender birthdate",
                "String String Character String", "name", null);
            
        test.addRelSchema ("movieExec", "certNo name address fee",
                "Integer String String Float", "certNo", null);
        
        test.addRelSchema ("studio", "name address presNo",
                "String String Integer", "name", null);
        
        test.addRelSchema ("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year",
                new String [][] {{ "studioName", "studio", "name" },
        		{"producerNo", "movieExec","certNo"}});
        
        test.addRelSchema ("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year",
                new String [][] {{ "studioName", "studio", "name" },
        		{"producerNo", "movieExec","certNo"}});
        
        test.addRelSchema ("starsIn", "movieTitle movieYear starName",
                "String Integer String", "movieTitle movieYear starName", 
                new String [][] {{ "movieTitle movieYear", "movie", "title year" },
        		{ "movieTitle movieYear", "cinema", "title year"},
        		{"starName", "movieStar", "name"}});
        
        String[] tables = new String [] { "movieStar", "movieExec", "studio","movie","cinema","starsIn"};
        int[] tups = new int [] {1000,1000,1000, 1000, 1000, 1000 };
        Comparable[][][] resultTest = test.generate (tups);
        
        ArrayList<Table> list=new ArrayList<Table>();
        list.add(movieStar);
        list.add(movieExec);
        list.add(studio);
        list.add(movie);
        list.add(cinema);
        list.add(starsIn);
        
                
        for (int i = 0; i < resultTest.length; i++) {
            for (int j = 0; j < resultTest [i].length; j++) {
                	Comparable[] result1 = resultTest[i][j];
                	list.get(i).insert(result1);
            } // for
        } // for
    

        Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
        Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
        Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
        Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
        out.println ();
        movie.insert (film0);
        movie.insert (film1);
        movie.insert (film2);
        movie.insert (film3);

        Comparable [] film4 = { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
        out.println ();
        cinema.insert (film2);
        cinema.insert (film3);
        cinema.insert (film4);
        out.println ("Cinema");

        Comparable [] star0 = { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" };
        Comparable [] star1 = { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
        Comparable [] star2 = { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
        out.println ();
        movieStar.insert (star0);
        movieStar.insert (star1);
        movieStar.insert (star2);
        out.println ("MovieStar");

        Comparable [] cast0 = { "Star_Wars", 1977, "Carrie_Fisher" };
        out.println ();
        starsIn.insert (cast0);
        out.println ("starsIn");

        Comparable [] exec0 = { 9999, "S_Spielberg", "Hollywood", 10000.00 };
        out.println ();
        movieExec.insert (exec0);
        out.println ("movieExec");

        Comparable [] studio0 = { "Fox", "Los_Angeles", 7777 };
        Comparable [] studio3 = { "Universal", "Universal_City", 8888 };
        Comparable [] studio2 = { "DreamWorks", "Universal_City", 9999 };
        out.println ();
        studio.insert (studio0);
        studio.insert (studio3);
        studio.insert (studio2);
        out.println ("studio");

//        movie.save ();
//        cinema.save ();
//        movieStar.save ();
//        starsIn.save ();
//        movieExec.save ();
//        studio.save ();



        //--------------------- Index select: key

        out.println ();
        long start_time_l = System.nanoTime();
        Table t_iselect_l = movieStar.select (new KeyType ("Harrison_Ford"));
        long end_time_l = System.nanoTime();
        double difference_l = (end_time_l - start_time_l) / 1e6;
        
        System.out.println("\r  time for LinHashMapIndex select key: "+difference_l+" second ");
   //     t_iselect_l.print ();
        out.println ();
        
        //--------------------- equi-join:  movie JOIN studio ON studioName = name

        long start_time1 = System.nanoTime();
        Table t_join = movie.join1 ("studioName", "name", studio);
        long end_time1 = System.nanoTime();
        double difference1 = (end_time1 - start_time1) / 1e6;
        System.out.println("\r<br> consuming time for join : "+difference1+" second ");
      //  t_join.print ();

    } // main

} // MovieDB class

