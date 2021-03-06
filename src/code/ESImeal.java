package code;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class ESImeal implements Serializable{
	
	
	public TreeSet<Commande> com = new TreeSet<Commande>();
	private TreeSet<Mois_jour> eventdates =  new TreeSet<Mois_jour>() ;
	public int nbcmd=com.size()+15;
	public HashMap<String,Client_fidele> cliens = new HashMap<String,Client_fidele>();
	public HashMap<String ,Client> cliens_normal = new  HashMap<String ,Client>();
	public HashMap<String,Repas> menu = new  HashMap<String,Repas>();
	public HashMap<String,Boisson> bois_v = new HashMap<String,Boisson>();
	public HashMap<String,Supplement> supps = new HashMap<String,Supplement>();
	
	public HashMap<String,Integer> quant_menu = new HashMap<String,Integer>();
	public HashMap<String,Integer> quant_bois = new HashMap<String,Integer>();
	
	//enum related
	private HashMap<String,Events> events = new HashMap<String,Events>();
	private HashMap<String,Type_Boisson> bois = new HashMap<String,Type_Boisson>();
	private HashMap<String,Type_Repas> reps = new HashMap<String,Type_Repas>();
	private HashMap<String,Place> place = new HashMap<String,Place>();
	public TreeMap<Integer,Table> tab = new TreeMap<Integer,Table>();
	///constructor
	public ESImeal() {
		
		events.put("anniversaire",Events.anniversaire);
				events.put("diplome",Events.diplome);
						events.put("mariage",Events.mariage);
								events.put("retraite",Events.retraite);
								
		bois.put("eau_minerale",Type_Boisson.eau_minerale);
		bois.put("jus",Type_Boisson.jus);
		bois.put("boisson_gazeuse",Type_Boisson.boisson_gazeuse);
		bois.put("cafe",Type_Boisson.cafe);
		bois.put("the",Type_Boisson.the);
		
		place.put("inter",Place.inter);
		place.put("exter",Place.exter);
		
		reps.put("entree",Type_Repas.entree);
		reps.put("plat",Type_Repas.plat);
		reps.put("dessert",Type_Repas.dessert);
		
		tab.put(1, new Table(1,5));
		tab.put(2, new Table(2,5));
		tab.put(3, new Table(3,5));
		
		
		
	}
	// valueof used here
	public void add_repas(String nom, boolean disponibilite, int nb_calories, double prix, Type_Repas type,int quant ) {
		//this.menu.remove(nom);
		menu.put(nom,new Repas(nom,disponibilite,nb_calories,prix,type));
		this.quant_menu.put(nom,quant);
	}
	public void add_boisson(String nom, int nb_calories, double prix, String gout, String marque,Type_Boisson type,int quant) {
		//this.bois.remove(nom);
		this.bois_v.put(nom,new Boisson(nom,true,nb_calories,prix,gout,marque,type));
		this.quant_bois.put(nom,quant);
	}
	public void add_supp(String nom, double prix, int nb_calories) {
		this.supps.put(nom,new Supplement(nom,prix,nb_calories));
	}
	
	public boolean sincrire(Client r,String nom, String prenom, String numero, boolean etudiant, String code_fidelite) {
		Client_fidele cl = new Client_fidele( nom,  prenom, numero,etudiant,  code_fidelite);
		if(r.getNb_commande()<3) {
			return false;
		}
		if(!(cliens.containsValue(cl))) {
			cliens.put(code_fidelite, cl);
			return true;
		}
		else {
			return false;
		}
		
	}
	
	
	
	/// making services
	public Service com_event(String d,int nb,String deco,Events e){
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime datet = LocalDateTime.parse(d, dt);
		Evenement kk = new Evenement(datet,nb,deco,e);
		return kk;
	}
	
	
	public Service com_C_surplace(String d,int nb,int t,String r) {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime datet = LocalDateTime.parse(d, dt);
		Place p =place.get(r);
		Table tt =tab.get(t);
		
		
		C_surplace kk = new C_surplace(datet,nb,tt,p);
		return kk;
	}
	
	public Service com_C_livre(String d,int nb,int dis, String adr) {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime datet = LocalDateTime.parse(d, dt);
		C_livre kk = new C_livre(datet,nb,dis,adr);
		return kk;
	}
	
	///connexion to enter
	public Client connexion(String nom, String prenom, String numero, boolean etudiant) {
		Client k = new Client(nom,prenom,numero,etudiant);
		return k;
	}
	
	public Client_fidele connect_get_client_fid(String code) {
		return cliens.get(code);
		// possible null
	}
	/// reponse sur la confirmation
	
	public String confirmation_msg(int id,Client cll,Service s) {
		try {
			boolean bool = false;
			
			try {
				C_livre mm = (C_livre) s; /// non verification des eventment
				mm.reduction(); // to test only
				bool=true;
			}
			catch(Exception e) {
				bool=false;
			}
			
			if(bool==true) { ///passage d'une commande livre pas la pein de verifier les evenements
				s.confirmer();
			//	HashMap<>
				//s.get_list();
				Commande kk = new Commande(id,cll,s);
				com.add(kk);
				return "commende effectu�";
			}
			else {
				int d = s.date.getDayOfMonth();
				int m = s.date.getMonthValue();
				Mois_jour test = new Mois_jour(m,d);
				if(eventdates.contains(test)) {
					throw new Dateexception();
				}
				
				s.confirmer();
				Commande kk = new Commande(id,cll,s);
				com.add(kk);
				try { ((C_surplace)s).table.use(s.date);
					
				}
				catch(Exception e) {
					eventdates.add(test);
				}
				cll.incr_nbcommande();
				return "commende effectu�";
				
				
				
			}
			
		}
		catch(Placeexception e) {
			return "place non disponible";
		}
		catch(Tableexception t) {
			return "table prise ";
		}
		catch(Eventpersonneexception r) {
			return "nombre de personne non autoris� ";
		}
		catch(Dateexception d) {
			return "date trop proche ou prise";
		}
		catch(Heureexception rr) {
			return "hors heures de travaille";
		}
		catch(Exception ee) {
			return "commande invalide";
		}
	}
	
	public void save_in_files_cliens_fidele() {
		BufferedWriter out =null;
		String ss="";
		String cl;
		Iterator it ;
		int i;
		Client_fidele ssl;
			
		try {
			out= new BufferedWriter(new FileWriter("cliens.txt"));
			for (Entry<String, Client_fidele> entry : cliens.entrySet())
			{	 	 cl = entry.getKey();  
						ssl = entry.getValue();  
						
						ss= ssl.getNom()+ "-"+ssl.getPrenom() +"-"+ssl.getNumero() +"-"+ssl.isEtudiant() +"-"+ssl.getNb_commande()+"-"+ssl.get_codefid() +"|";
						it = ssl.adresse.iterator();
						while(it.hasNext()) {
							ss=ss+it.next()+"*";
						}
						out.write(ss);
						out.newLine();
		
			}
			out.flush();
			out.close();
			
		} catch (IOException e) {
			
			
			//e.printStackTrace();
		}
		
	}

	public void load_out_files_cliens_fidele() {
		try {
			String ss;
			String[] grad;
			String[] object;
			String[] adr;
			Client_fidele kf;
			int i;
			ArrayList<String> ls = new ArrayList<String>();
			BufferedReader in = new BufferedReader(new FileReader("cliens.txt"));
			cliens.clear();
			ss=in.readLine();
			while(ss!=null) {
				grad=ss.split(Pattern.quote("|"));
				object=grad[0].split(Pattern.quote("-"));
				ls.clear();
				try {
					adr=grad[1].split(Pattern.quote("*"));
					for(i=0;i<adr.length;i++) {
						ls.add(adr[i]);
					}
					
				}
				catch(Exception e) {
					
				}
				kf= new Client_fidele(object[0],object[1],object[2],Boolean.parseBoolean(object[3]),object[5]) ;
				kf.adresse.addAll(ls);
				kf.set_nb_cmd(Integer.parseInt(object[4]));
				cliens.put(object[4],kf );
				
				
				ss=in.readLine();
			}
			
			
			
		}
		catch(Exception e) {
			
		}
	}
	
	public void save_all() {
		
		ESImeal ssl;
		ssl=this;
		ObjectOutputStream out;
		try {
			out= new ObjectOutputStream( new BufferedOutputStream(new FileOutputStream (new File("esimeal.txt") )));
			
			out.writeObject(ssl);
			out.flush();
			out.close();
			
		} catch (IOException e) {
			
			
			//e.printStackTrace();
		}
	}
	public ESImeal load_all() {
		ESImeal ssl;
		ObjectInputStream in;
		
		
		cliens_normal.clear();
		try {
				in = new ObjectInputStream( new BufferedInputStream(new FileInputStream (new File("esimeal.txt") )));
			
				ssl=((ESImeal)in.readObject());
				
				return (ssl);
			
		
			
			
			
		}
		catch(Exception e) {
			return null;
		}
		
		
		
		
		
	}



	



	
	
	
	
	public void save_in_files_cliens_n() {
		
		String ss="";
		String cl;
		
		int i;
		Client ssl;
		ObjectOutputStream out;
		try {
			out= new ObjectOutputStream( new BufferedOutputStream(new FileOutputStream (new File("client_normal.txt") )));
			
			for (Entry<String, Client> entry : cliens_normal.entrySet())
			{	 	 cl = entry.getKey();  
						ssl = entry.getValue();  
						out.writeObject(ssl);
						
		
			}
			out.flush();
			out.close();
			
		} catch (IOException e) {
			
			
			//e.printStackTrace();
		}
		
	}

	public void load_out_files_cliens_n() {
		ObjectInputStream in;
		Client ssl;
		String key;
		cliens_normal.clear();
		try {
			in = new ObjectInputStream( new BufferedInputStream(new FileInputStream (new File("client_normal.txt") )));
			while(true) {
				ssl=((Client)in.readObject());
				cliens_normal.put(ssl.getNom()+ssl.getPrenom()+ssl.getNumero(), ssl);
			}
		
			
			
			
		}
		catch(Exception e) {
			
		}
	}
	
	public double mnt_reduction(){
		Iterator<Commande>it  = com.iterator();
	    Commande h;
	    double mnt_red;
	    double red=0;
	    double mnt=0;
		while( it.hasNext()){
			h=it.next();
			red=red+h.calculer_reduction(h.getClient());
			mnt=mnt+h.calculer_prix();
			}
		 mnt_red = red*mnt;
		return(mnt_red);
	};
	public int nb_cmd()
	{
		 Commande c;
		    int mnt=0;
		    Iterator<Commande>cmd  = com.iterator();
		    while( cmd.hasNext()){
				c=cmd.next();
				mnt++;
				}
		    return (mnt);
	}
	public double montant_tot()
	{
		 Commande c;
		    double mnt=0;
		    Iterator<Commande>cmd  = com.iterator();
		    while( cmd.hasNext()){
				c=cmd.next();
				mnt=mnt+c.calculer_prix();
				}
		    return (mnt);
	}
	public double mnt_reduction_fidele(){
		Iterator<Client_fidele>it  = cliens.values().iterator();
	   Client_fidele h;
	  double mnt_red_fid=0;
	    double red=0;
	    
		while( it.hasNext()){
			h=it.next();
			red=red+h.reduction();
			
			}
		mnt_red_fid=red*this.montant_tot();
		return(mnt_red_fid);
	};
	public double mnt_reduction_Etudiant(){
		Iterator<Commande>it  =com.iterator();
	   Commande h;
	   double mnt_red_et;
	    double red=0;
	    
		while( it.hasNext()){
			h=it.next();
			red=red+h.getClient().reduction();
			
			}
		 mnt_red_et = red*this.montant_tot();
		return(mnt_red_et);
	};
	
	
	
	
	 public String getClientLePlusCommande ()
	    {
	        Iterator<Commande>it=com.iterator();
	        int i =0 ;
	        int grand = 0;
	        String id="";
	        while (it.hasNext()) {
	            Client c = it.next().getClient();
	            {
	                i=c.getNb_commande();
	                if(i>grand){grand=i;id = c.getNom();}
	            }
	        }
	        return (id );
	    }
	 public String getClientLePlusred ()
	    {
	        Iterator<Commande>it=com.iterator();
	        double i =0 ;
	        double grand = 0;
	        String id="";
	        while (it.hasNext()) {
	            Client c = it.next().getClient();
	            {
	                i=c.reduction();
	                if(i>grand){grand=i;id = c.getNom();}
	            }
	        }
	        return (id );
	    }
	 public String getClientLeMoinsCommande ()
	    {
	        Iterator<Commande>it=com.iterator();
	        int i =0 ;
	        int petit = 0;
	        String id="";
	        while (it.hasNext()) {
	            Client c = it.next().getClient();
	            {
	                i=c.getNb_commande();
	                if(i<petit){petit=i;id = c.getNom();}
	            }
	        }
	        return id ;
	    }
	
	public void set_all_fortest() {

		
		
		
		Repas menu1 =new Repas("poulet",true,200,200,Type_Repas.plat);
		Repas menu2 =new Repas("pizza",true,250,250,Type_Repas.plat);
		Repas menu3 =new Repas("chawarma",false,300,250,Type_Repas.plat);
		menu.put("pizza", menu2);
		menu.put("poulet", menu1);
		menu.put("chawarma",menu3);
		Boisson menu4 =new Boisson("eau",true,200,200,"normale","iffri",Type_Boisson.eau_minerale);
		Boisson menu5 =new Boisson("cappochino",true,250,250,"sans_sucre","Niscafe",Type_Boisson.cafe);
		Boisson menu6 =new Boisson ("jus_d'orange",false,300,250,"light","Rami",Type_Boisson.jus);
		bois_v.put("eau", menu4);
		bois_v.put("cappochino", menu5);
		bois_v.put("jus_d'orange",menu6);
		
		Service cs1 = com_C_surplace("2018-12-10 01:01",5, 1, "inter");
		Client cll1 = connexion("me", "metoo", "02164646", true);
		Service cs2 = com_C_surplace("2018-01-01 01:01",5, 1, "exter");
		
		Service cs8 = com_C_surplace("2018-01-02 01:01",5, 1, "exter");
		
		Client cll2 = connexion("tchoulak", "Nassim"," 05164134", false);
		Service cs3 = com_C_surplace("2018-05-10 18:10",5, 1, "inter");
		cs3.menu.add(new Repas("cappuchino", true, 150, 200,Type_Repas.entree));
		cs3.menu.add(new Repas("cappuchino", true, 150, 200,Type_Repas.entree));
		Client cll3 = connexion("kouici", "imene", "046664615", false);
		this.cliens_normal.put(cll1.getNom()+cll1.getPrenom()+cll1.getNumero(), cll1);
		this.cliens_normal.put(cll2.getNom()+cll2.getPrenom()+cll2.getNumero(), cll2);
		this.cliens_normal.put(cll3.getNom()+cll3.getPrenom()+cll3.getNumero(), cll3);
		Commande cmd1=new Commande(1,cll1,cs1);
		Commande cmd2=new Commande(7,cll2,cs2);
		Commande cmd3=new Commande(9,cll3,cs3);
		Commande cmd4 =new Commande(18,cll2,cs8);
		com.add(cmd4);
		 com.add(cmd1);
		 com.add(cmd2);
		 com.add(cmd3);
		
		 cll2.incr_nbcommande();
		 cll2.incr_nbcommande();
		 
		 cll1.incr_nbcommande();
		 cll3.incr_nbcommande();
		 
		 Mois_jour t1=new Mois_jour(12,10);
		 Mois_jour t2=new Mois_jour(01,01);
		 Mois_jour t3=new Mois_jour(06,03);
		  eventdates.add(t1);
		  eventdates.add(t2);
		  eventdates.add(t3);
		Client_fidele c1=new Client_fidele("me", "metoo", "02164646", true,"dfd");
		Client_fidele c2=new Client_fidele("Tchoulak", "Nassim","02555", false,"sdsdcsc");
		Client_fidele c3=new Client_fidele("Kouici", "Imene", "026461231", true,"ezezd");
		cliens.put("sdsdcsc", c2);
		cliens.put("ezezd", c3);
		cliens.put("dfd", c1);
		
		Supplement ss1 = new Supplement("cheese",12.5,4);
		Supplement ss2 = new Supplement("viande",16.0,4);
		Supplement ss3 = new Supplement("poulet",16.9,4);
		
		supps.put(ss1.getNom(), ss1);
		supps.put(ss2.getNom(), ss2);
		supps.put(ss3.getNom(), ss3);
	}
	
	public ArrayList<Repas> get_repass(){
		Repas ssl;
		ArrayList<Repas> aa = new ArrayList<Repas>();
		for (Entry<String, Repas> entry : this.menu.entrySet())
		{	 	 
					ssl = entry.getValue();  
					aa.add(ssl);
		}
		return aa;
	}
	
	
	public static void main(String[] args) {
		ESImeal s = new ESImeal();
		
			Service cs = s.com_C_surplace("2018-06-08 12:01",5, 1, "inter");
			
		Client cll = s.connexion("me", "metoo", "02164646", true);
		System.out.println(s.confirmation_msg(1, cll, cs));
		Service cs2 = s.com_C_surplace("2018-06-08 14:05",5, 1, "inter");
		//cs2.menu.add(new Repas(""));
		System.out.println(s.confirmation_msg(1, cll, cs2));
		System.out.println( ((Table)s.tab.get(1)).his.pollFirst()   );
		LocalDateTime h = LocalDateTime.now();
		s.set_all_fortest();
	
		C_livre c = new C_livre(h,1,10,"here");
		try {
			c.confirmer();
		} catch (Placeexception e) {
			// TODO Auto-generated catch block
			System.out.print("place");
		}
		catch(Dateexception e) {
			//e.printStackTrace();
		}
		catch(Exception e) {
			
		}
		s.set_all_fortest();
		   System.out.println("Le nombre  de toutes les commandes effectu�es:"+s.nb_cmd());
	       System.out.println("Le montant de toutes les commandes effectu�es:"+s.montant_tot());
	       System.out.println("Le montant total des reductions offretes:"+s.mnt_reduction());
	       System.out.println("Le montant total des reductions de type etudiant"+s.mnt_reduction_Etudiant());
	       System.out.println("Le montant total des reductions de type fidele"+s.mnt_reduction_fidele());
	      
	       System.out.println("Le client ayant effectue le plus de commande:"+s.getClientLePlusCommande());
	       System.out.println("Le client ayant effectue le moins de commande:"+s.getClientLeMoinsCommande());
	    //   System.out.println("Le client ayant effectue le plus de reduction:"+s.);
		
	}
}
