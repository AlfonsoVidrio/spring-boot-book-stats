package com.alura.desafio.main;

import com.alura.desafio.model.BooksData;
import com.alura.desafio.model.Data;
import com.alura.desafio.service.Api;
import com.alura.desafio.service.DataConverter;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final String BASE_URL = "https://gutendex.com/books/";
    private Api api = new Api();
    private DataConverter dataConverter = new DataConverter();
    private Scanner sn = new Scanner(System.in);

    public void showMenu() {
        var json = api.getData(BASE_URL);
        //System.out.println(json);
        var data = dataConverter.getData(json, Data.class);
        System.out.println(data);

        // Top 10 most downloaded books
        System.out.println("Top 10 libros más descargados");
        data.books().stream()
                .sorted(Comparator.comparing(BooksData::downloadCount).reversed())
                .limit(10)
                .map(b -> b.title().toUpperCase())
                .forEach(System.out::println);

        // Search for books by name
        System.out.print("Ingrese el nombre del libro que se desea buscar: ");
        var bookName = sn.nextLine();
        json = api.getData(BASE_URL + "?search=" + bookName.replace(" ", "+"));
        var bookSearch = dataConverter.getData(json, Data.class);
        Optional<BooksData> book = bookSearch.books().stream()
                .filter(b -> b.title().toUpperCase().contains(bookName.toUpperCase()))
                .findFirst();

        if(book.isPresent()) {
            System.out.println("Libro encontrado: " + book.get());
        } else {
            System.out.println("Libro no encontrado");
        }


        // Libro con más descargas
        BooksData mostDownloadedBook = data.books().stream()
                .max(Comparator.comparing(BooksData::downloadCount))
                .orElseThrow(() -> new RuntimeException("No books found"));
        System.out.println("Libro con más descargas: " + mostDownloadedBook.title() + " con " + mostDownloadedBook.downloadCount() + " descargas");

        // Libro con menos descargas
        BooksData leastDownloadedBook = data.books().stream()
                .min(Comparator.comparing(BooksData::downloadCount))
                .orElseThrow(() -> new RuntimeException("No books found"));
        System.out.println("Libro con menos descargas: " + leastDownloadedBook.title() + " con " + leastDownloadedBook.downloadCount() + " descargas");

        // Working with statistics
        System.out.println("\nEstadísticas de descargas");
        DoubleSummaryStatistics statistics = data.books().stream()
                .filter(d -> d.downloadCount() > 0)
                .collect(Collectors.summarizingDouble(BooksData::downloadCount));
        System.out.println("Media de descargas: " + statistics.getAverage());
        System.out.println("Libro con más descargas: " + statistics.getMax());
        System.out.println("Libro con menos descargas: " + statistics.getMin());
        System.out.println("Total de descargas: " + statistics.getSum());
    }
}
