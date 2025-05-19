package org.example;

import DataAcces.ClientDAO;
import Model.Client;

import DataAcces.ProductDAO;
import Model.Product;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Product P1 = new Product(1, "Milka", 2, 10);
        ProductDAO dao = new ProductDAO();

        dao.insert(P1);
    }
}