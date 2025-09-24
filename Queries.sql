USE inventoryDB;
INSERT INTO products VALUES (1, 'Laptop', 'Electronics', 10, 55000), (4, 'Chair', 'Furniture', 20, 2500), (9, 'Pen', 'Stationery', 100, 10), (10, 'Phone', 'Electronics', 15, 30000), (7, 'Table', 'Furniture', 5, 8000);
SELECT * FROM products;
SELECT Name, Category FROM products;
SELECT * FROM products
WHERE Quantity > 10;
SELECT * FROM products
WHERE Price < 5000;
SELECT * FROM products
WHERE Category = 'Electronics';
SELECT * FROM products
ORDER BY Price DESC;
SELECT * FROM products
ORDER BY Price DESC
LIMIT 3;
SELECT SUM(Quantity) AS Total_Products
FROM products;
SELECT AVG(Price) AS Average_Price
FROM products;
SELECT * FROM products
WHERE Price = (SELECT MAX(Price) FROM products);