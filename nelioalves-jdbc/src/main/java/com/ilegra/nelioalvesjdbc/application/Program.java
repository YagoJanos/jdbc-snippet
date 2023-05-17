package com.ilegra.nelioalvesjdbc.application;

import com.ilegra.nelioalvesjdbc.db.DB;
import com.ilegra.nelioalvesjdbc.db.exception.DBIntegrityException;
import com.ilegra.nelioalvesjdbc.db.exception.DbException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Program {
    public static void main(String[] args) {

        //Fazendo busca

        try (Connection connection = DB.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from department"))   {

            while (rs.next()){
                System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } catch (DbException e){
            e.printStackTrace();
        }




        //Inserindo uma nova linha e retornando quantas linhas foram afetadas no banco de dados

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO seller "
                     + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                     + "VALUES "
                     + "(?, ?, ?, ?, ?)"
             )) {

            preparedStatement.setString(1, "a");
            preparedStatement.setString(2,"carl@gmail.com");
            preparedStatement.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
            preparedStatement.setDouble(4, 3000.0);
            preparedStatement.setInt(5, 4);

            int rowsAffected = preparedStatement.executeUpdate();

            System.out.println("Done! Rows affected: " + rowsAffected);

        } catch (SQLException e){
            e.printStackTrace();
        } catch (DbException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }





        //Inserindo uma linha e retornando o id dessa linha

        try (Connection connection = DB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO seller "
                             + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                             + "VALUES "
                             + "(?, ?, ?, ?, ?)"
             , Statement.RETURN_GENERATED_KEYS)
            ) {

            preparedStatement.setString(1, "a");
            preparedStatement.setString(2,"carl@gmail.com");
            preparedStatement.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
            preparedStatement.setDouble(4, 3000.0);
            preparedStatement.setInt(5, 4);

            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0){
                try(ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    while (rs.next()){
                        int id = rs.getInt(1);
                        System.out.println("Done! Id = " + id);
                    }
                }
            } else {
                System.out.println("No rows affected");
            }

            System.out.println("Done! Rows affected: " + rowsAffected);

        } catch (SQLException e){
            e.printStackTrace();
        } catch (DbException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }





        //Inserindo várias linhas e retornando os ids.

        try(Connection connection = DB.getConnection();
            PreparedStatement pst = connection.prepareStatement("INSERT INTO department (Name) " +
            "values ('D1'),('D2')", Statement.RETURN_GENERATED_KEYS)){

            int rowsAffected = pst.executeUpdate();

            if(rowsAffected > 0){
                try(ResultSet rs = pst.getGeneratedKeys()) {
                    while (rs.next()){
                        int id = rs.getInt(1);
                        System.out.println("Done! Id = " + id);
                    }
                }
            } else {
                System.out.println("No rows affected");
            }

            System.out.println("Done! Rows affected: " + rowsAffected);

        } catch (SQLException e){
            e.printStackTrace();
        } catch (DbException e){
            e.printStackTrace();
        }



        //Atualizar um registro do banco de dados
        try(Connection conn = DB.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE seller "
                    + "SET BaseSalary = BaseSalary + ? "
                    + "WHERE "
                    + "(DepartmentId = ?)"))
        {
            pst.setDouble(1, 200.0);
            pst.setInt(2,2);

            int rowsAffected = pst.executeUpdate();

            System.out.println("Done! Rows affected: " + rowsAffected);

        }catch (SQLException e){
            e.printStackTrace();
        }





        //Deletar dados

        try(Connection connection = DB.getConnection();
            PreparedStatement pst = connection.prepareStatement("DELETE FROM department " +
                    "WHERE "+
                    "Id = ?"))
        {
            pst.setInt(1, 5);

            int rowsAffected = pst.executeUpdate();

            System.out.println("Done! Rows affected: " + rowsAffected);
        } catch(SQLException e){
            throw new DBIntegrityException(e.getMessage());
        }





        // transações

        Connection connection = null;
        Statement st = null;

        try{
            connection = DB.getConnection();
            st = connection.createStatement();
            connection.setAutoCommit(false);

            int rows1 = st.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1");

            int x = 1;
            if (x < 2){
                throw  new SQLException("Fake error");
            }

            int rows2 = st.executeUpdate("UPDATE seller SET BaseSalary = 3090 WHERE DepartmentId = 2");

            connection.commit();

            System.out.println("rows1: " + rows1);

            System.out.println("rows2: " + rows2);
        }catch(SQLException e){
            try {
                connection.rollback();
                throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
            } catch (SQLException ex) {
                throw new DbException("Error trying to roll back! Caused by: " + e.getMessage());
            }
        } finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }
}