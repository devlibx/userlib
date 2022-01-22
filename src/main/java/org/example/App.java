package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.distribution.PoissonDistribution;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        PoissonDistribution distribution = new PoissonDistribution(10, 0);
        for(int i = 0 ; i < 0; i++) {
            System.out.println(distribution.sample());
        }

        String[] HEADERS = { "author", "title"};
        FileWriter out = new FileWriter("book_new.csv");
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            for(int i = 0 ; i < 1000000; i++) {
                printer.printRecord(i, distribution.sample());
            }
        }
    }
}
