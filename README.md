# Warehouse Management System

This Java desktop application is designed to manage **warehouse inventory and customer orders**. It provides an intuitive GUI for handling products, clients, and orders, while ensuring data persistence using a MySQL database.

## ⚙️ Features

* Manage **Products** (add, update, delete, view stock)
* Manage **Clients**
* Create **Orders**
* Automatic **Bill generation**
* Stock validation (prevents under-stock orders)
* Real-time updates between modules

## 🧱 Architecture

The project follows a layered architecture:

* **Presentation Layer (GUI)**

  * `ClientGUI`, `ProductGUI`, `OrderGUI`
* **Business Logic Layer**

  * Validation and business rules (`ClientBLL`, `ProductBLL`, `OrderBLL`)
* **Data Access Layer**

  * Generic DAO (`AbstractDAO`)
  * Specific DAOs (`ClientDAO`, `ProductDAO`, `OrderDAO`, `BillDAO`)
* **Model**

  * `Client`, `Product`, `Order`, `Bill`

## 🗄️ Database

* MySQL database: `ordersmanagement`
* Uses JDBC via `ConnectionFactory`
* Tables:

  * `client`
  * `product`
  * `orders`
  * `bill`

## 🧠 Key Concepts

* Java Swing GUI
* Reflection-based DAO pattern
* JDBC database connectivity
* MVC-like architecture
* Data validation and synchronization between views
