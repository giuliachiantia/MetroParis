package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.CoppiaId;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {
	
	public List <Fermata> getFermateConnesse(Fermata partenza){
		final String sql = "select id_fermata, nome, coordx, coordy "
				+ "from fermata "
				+ "where id_fermata in ("
				+ "select id_stazA "
				+ "from connessione "
				+ "where id_stazP=? "
				+ ")"
				+ "order by nome ASC ";
		
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}
	
	public boolean isFermataConnessa(Fermata partenza, Fermata arrivo) {
		 String sql="select count (*) as cnt "
		 		+ "from connessione "
		 		+ "where id_stazP=? and id_stazA=?";
		 
		 Connection conn= DBConnect.getConnection();
		 try {
			 PreparedStatement st = conn.prepareStatement(sql);
			 st.setInt(1, partenza.getIdFermata());
			 st.setInt(2, arrivo.getIdFermata());
			 ResultSet res = st.executeQuery();
			 res.first();
			 
			 int count=res.getInt("cnt");
			 conn.close();
			return count>0;
			
			} catch (SQLException e) {

				e.printStackTrace();
				throw new RuntimeException(e);
			}
		 
		 		
	}

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}
	
	public List <Integer> getIdFermateConnesse(Fermata partenza){
		final String sql = "select id_stazA "
				+ "from connessione "
				+ "where id_stazP=? "
				+ "group by id_stazA";
		

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			ResultSet res = st.executeQuery();
			List <Integer> result= new ArrayList();
			
			while (res.next()) {
				
				result.add(res.getInt("id_stazA"));
				
			}

			st.close();
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			//throw new RuntimeException("Errore di connessione al Database.");
			return null;
		}

		
		
	}
	
	public List<CoppiaId> getAllFermateConnesse(){
		final String sql = "select distinct id_stazP, id_stazA "
				+ "from connessione";
		Connection conn = DBConnect.getConnection();
		
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			List <CoppiaId> result= new ArrayList<>();
			
			while (res.next()) {
				
				result.add(new CoppiaId(res.getInt("id_stazP"), res.getInt("id_stazA")));
				
			}

			st.close();
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			//throw new RuntimeException("Errore di connessione al Database.");
			return null;
		}

		
	}
	
	

	

}
