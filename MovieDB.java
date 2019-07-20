package p4;
/*****************************************************************************************
 * @file  MovieDB.java
 *
 * @author   John Miller
 */

import java.util.ArrayList;

import static java.lang.System.out;

/*****************************************************************************************
 * The MovieDB class makes a Movie Database.  It serves as a template for making other
 * databases.  See "Database Systems: The Complete Book", second edition, page 26 for more
 * information on the Movie Database schema.
 */
class MovieDB {
    /*************************************************************************************
     * Main method for creating, populating and querying a Movie Database.
     * @param args  the command-line arguments
     */
    public static void main(String[] args) {
        out.println();

        Table movie = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        Table cinema = new Table("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        Table movieStar = new Table("movieStar", "name address gender birthdate",
                "String String String String", "name");

        Table starsIn = new Table("starsIn", "movieTitle movieYear starName",
                "String Integer String", "movieTitle movieYear starName");

        Table movieExec = new Table("movieExec", "certNo name address fee",
                "Integer String String Double", "certNo");

        Table studio = new Table("studio", "name address presNo",
                "String String Integer", "name");

        Table [] hollywood = new Table[] {movie, cinema, movieStar, starsIn, movieExec, studio};

        TupleGeneratorImpl test = new TupleGeneratorImpl();

        test.addRelSchema("movieExec", "certNo name address fee",
                "Integer String String Double", "certNo", null);

        test.addRelSchema("studio", "name address presNo",
                "String String Integer", "name", null);

        test.addRelSchema("movieStar", "name address gender birthdate",
                "String String String String", "name", null);

        test.addRelSchema("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year",
                new String[][]{{"studioName", "studio", "name"},
                        {"producerNo", "movieExec", "certNo"}});

        test.addRelSchema("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year",
                new String[][]{{"studioName", "studio", "name"},
                        {"producerNo", "movieExec", "certNo"}});

        test.addRelSchema("starsIn", "movieTitle movieYear starName",
                "String Integer String", "movieTitle movieYear starName",
                new String[][]{{"movieTitle movieYear", "movie", "title year"},
                        {"movieTitle movieYear", "cinema", "title year"},
                        {"starName", "movieStar", "name"}});


        String[] tables = new String[]{"movie", "cinema", "movieStar", "starsIn", "movieExec", "studio"};
        int[] tups = new int[]{1000, 1000, 1000, 1000, 1000, 1000};
        Comparable[][][] resultTest = test.generate(tups);

        ArrayList<Table> list = new ArrayList<Table>();
        list.add(movieExec);
        list.add(studio);
        list.add(movieStar);
        list.add(movie);
        list.add(cinema);
        list.add(starsIn);


        for (int i = 0; i < resultTest.length; i++) {
            out.println (tables [i]);
            for (int j = 0; j < resultTest [i].length; j++) {
       //         for (int k=0; k<resultTest[i][j].length; k++) {
       //             out.print(resultTest[k] + ",");
                    Comparable [] result1 = resultTest[i][j];
                    list.get(i).insert(result1);
                 //
      //          } // for
                out.println ();
            } // for
            out.println ();
        } // for


            Comparable[] film0 = {"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
            Comparable[] film1 = {"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
            Comparable[] film2 = {"Rocky", 1985, 200, "action", "Universal", 12125};
            Comparable[] film3 = {"Rambo", 1978, 100, "action", "Universal", 32355};
            out.println();
            movie.insert(film0);
            movie.insert(film1);
            movie.insert(film2);
            movie.insert(film3);
            movie.print();

            Comparable[] film4 = {"Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890};
            out.println();
            cinema.insert(film2);
            cinema.insert(film3);
            cinema.insert(film4);
            cinema.print();

            Comparable[] star0 = {"Carrie_Fisher", "Hollywood", 'F', "9/9/99"};
            Comparable[] star1 = {"Mark_Hamill", "Brentwood", 'M', "8/8/88"};
            Comparable[] star2 = {"Harrison_Ford", "Beverly_Hills", 'M', "7/7/77"};
            out.println();
            movieStar.insert(star0);
            movieStar.insert(star1);
            movieStar.insert(star2);
            movieStar.print();

            Comparable[] cast0 = {"Star_Wars", 1977, "Carrie_Fisher"};
            out.println();
            starsIn.insert(cast0);
            starsIn.print();

            Comparable[] exec0 = {9999, "S_Spielberg", "Hollywood", 10000.00};
            out.println();
            movieExec.insert(exec0);
            movieExec.print();

            Comparable[] studio0 = {"Fox", "Los_Angeles", 7777};
            Comparable[] studio1 = {"Universal", "Universal_City", 8888};
            Comparable[] studio2 = {"DreamWorks", "Universal_City", 9999};
            out.println();
            studio.insert(studio0);
            studio.insert(studio1);
            studio.insert(studio2);
            studio.print();
            /*
            movie.save();
            cinema.save();
            movieStar.save();
            starsIn.save();
            movieExec.save();
            studio.save();
            */
            movieStar.printIndex();

            //--------------------- project: title year

            out.println();
            Table t_project = movie.project("title year");
            t_project.print();

            //--------------------- select: equals, &&

            out.println();
            Table t_select = movie.select(t -> t[movie.col("title")].equals("Star_Wars") &&
                    t[movie.col("year")].equals(1977));
            t_select.print();

            //--------------------- select: <

            out.println();
            Table t_select2 = movie.select(t -> (Integer) t[movie.col("year")] < 1980);
            t_select2.print();

            //--------------------- indexed select: key

            out.println();
            Table t_iselect = movieStar.select(new KeyType("Harrison_Ford"));
            t_iselect.print();

            //--------------------- union: movie UNION cinema

            out.println();
            Table t_union = movie.union(cinema);
            t_union.print();

            //--------------------- minus: movie MINUS cinema

            out.println();
            Table t_minus = movie.minus(cinema);
            t_minus.print();

            //--------------------- equi-join: movie JOIN studio ON studioName = name

            out.println();
            Table t_join = movie.join("studioName", "name", studio);
            t_join.print();

            //--------------------- natural join: movie JOIN studio

            out.println();
            Table t_join2 = movie.join(cinema);
            t_join2.print();

        } // main

    }
