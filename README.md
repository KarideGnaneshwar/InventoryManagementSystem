<h1 align="center">📦 Inventory Management System</h1>

<p align="center">
  <b>A Core Java desktop application for managing products, sales, and staff activity.</b><br>
  Built with <b>Java Swing</b>, <b>JDBC</b>, and <b>MySQL</b> using an MVC-style layered design.
</p>

<hr>

<h2>🌟 Overview</h2>

<p>
  <b>Inventory Management System</b> is a Java-based desktop application that helps businesses
  manage their stock, sales, and basic user activity from a single interface. It provides
  product CRUD operations, sales handling, reporting, CSV export, and secure login with
  hashed passwords.
</p>

<hr>

<h2>🚀 Features</h2>

<ul>
  <li>🔐 <b>Login system</b> using <code>LoginForm</code>, <code>LoginDAO</code>, and secure hashing (<code>HashUtil</code>).</li>
  <li>📦 <b>Product management</b>: add, update, delete, and view products via <code>ProductForm</code> and <code>ProductController</code>.</li>
  <li>🧾 <b>Sales management</b>: record and view sales using <code>SalesView</code>, <code>SaleForm</code>, and <code>SalesController</code>.</li>
  <li>📊 <b>Reports & analytics</b>: sales reports and staff performance views
      (<code>SalesReportView</code>, <code>StaffPerformanceView</code>).</li>
  <li>📁 <b>CSV export</b> for reports and data backup using <code>CSVUtil</code>.</li>
  <li>📝 <b>Activity logging</b> using <code>ActivityLogger</code> for audit and tracking.</li>
  <li>🖥️ <b>Central dashboard</b> via <code>MainFrame</code> for navigating between inventory and sales modules.</li>
</ul>

<hr>

<h2>🧩 Tech Stack</h2>

<table>
  <tr><td><b>Language</b></td><td>Core Java (JDK 17)</td></tr>
  <tr><td><b>UI</b></td><td>Java Swing</td></tr>
  <tr><td><b>Database</b></td><td>MySQL (via JDBC)</td></tr>
  <tr><td><b>Architecture</b></td><td>MVC-inspired layered design (controller / dao / model / view / util)</td></tr>
  <tr><td><b>IDE</b></td><td>Eclipse</td></tr>
</table>

<hr>

<h2>📂 Project Structure</h2>

<pre>
InventoryManagementSystem/
└── src/
    ├── controller/
    │   ├── ProductController.java
    │   └── SalesController.java
    │
    ├── dao/
    │   ├── LoginDAO.java
    │   ├── ProductDAO.java
    │   └── SalesDAO.java
    │
    ├── main/
    │   └── Main.java
    │
    ├── model/
    │   ├── Product.java
    │   ├── Sale.java
    │   └── User.java
    │
    ├── util/
    │   ├── ActivityLogger.java
    │   ├── CSVUtil.java
    │   ├── DBConnection.java
    │   └── HashUtil.java
    │
    └── view/
        ├── LoginForm.java
        ├── MainFrame.java
        ├── ProductForm.java
        ├── SaleForm.java
        ├── SalesReportView.java
        ├── SalesView.java
        ├── StaffPerformanceView.java
        └── StaffProductView.java
</pre>

<hr>

<h2>🧭 Future Enhancements</h2>

<ul>
  <li>📱 Modernized UI using JavaFX for richer visuals.</li>
  <li>🌐 Multi-user network mode (server–client) for use across multiple systems.</li>
  <li>📈 Advanced analytics dashboards for product trends and revenue insights.</li>
  <li>☁️ Optional cloud database support for remote backups and access.</li>
</ul>

<hr>



<p align="center">
  ⭐ If you find this project useful, please consider giving it a star on GitHub!
</p>
