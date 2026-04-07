package com.academico;

import com.academico.db.DatabaseManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        System.out.println("Inicializando conexión...");

        DatabaseManager.initialize("localhost", 5432, 
                                   "registro_academico", "postgres", "Sol11ADAN!");

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT clave, valor FROM configuracion")) {

            System.out.println("Conexión exitosa. Configuración del sistema:");
            while (rs.next()) {
                System.out.println("  " + rs.getString("clave") 
                                 + " = " + rs.getString("valor"));
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            DatabaseManager.close();
        }
    }
}