package code;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.*;
public class Commande implements Comparable<Commande>, Serializable{
	
private int id;

private Service service;
private String client_code;
private Client client;
private boolean vip; // si client d'un client fidele ou pas 

public Service getService(){
	return  service;
}
public Commande(int id,Client c ,Service s) {
	this.id=id;
	setClient(c);
	client_code=c.getNom()+c.getPrenom()+c.getNumero();
	service=s;
	try { 
		 ((Client_fidele)c).afficher_info();
		 vip=true;
	}catch(Exception e){
		vip=false;
	}
}
public int getid() {
	return id;
}


public void ajouter_met(Met met) {
	try{
	service.menu.add(met);}
	catch (Exception e){
		
	}
	
}


public void supp_met(Met met){
	try{
		service.menu.remove(met);}
		catch (Exception e){
			
		}
}
public double calculer_prix(){
	Iterator<Met>it  = service.menu.iterator();
	double p=0;
	Met s;
	System.out.println();
	while( it.hasNext()){
		s=it.next();
		p=p+s.Calculer_prix();
		System.out.println(p);
	}
	return(service.sur_plus(p));/// calcule prix avec surplus direct
	
}
		
public double calculer_reduction(Client client) {// send client from hash map in parameter
	return client.reduction()+service.reduction();
}


public double prix_a_payer(Client client) {
	return calculer_prix()*(1-calculer_reduction(client));
}


public void set_client(String s) {
	client_code=s;
}
public String get_client() {
	return client_code;
}
public boolean get_vip() {
	return vip;
}

public boolean equals(Commande k) {
	if( k.id==this.id) {
		return true;
	}
	else {
		return false;
	}
}
public int compareTo(Commande k ) {
	return this.service.date.compareTo(k.service.date);
}
public Client getClient() {
	return client;
}
public void setClient(Client client) {
	this.client = client;
}







}
