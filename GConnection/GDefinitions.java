package GConnection;

class SessionInfo {
	public String[] server_info;			/* tablica z adresem ip (server_info[0]) oraz numerem portu (server_info[1])*/
	public String g_password;				/* haslo gg*/
	public int g_number;					/* mój numerek */
	public int g_seed;						/* seed odczytany z pakietu, do obliczenia hash*/
    public int g_hash;						/* hash has?a */
    public int g_status 		= 0x0002;	/* status na dzien dobry */
    public int g_version 		= 0x40000024;		/* moja wersja klienta: 6.1 (build 155)*/
    public int unknown1 		= 0x00;		/* char z C, 0x00 */
    public int local_ip 		= 0x0200a8c0;	/* mój adres ip */
    public short local_port 	= 0x00;		/* port, na którym s?ucham */
    public int external_ip 		= 0x0000;	/* zewn?trzny adres ip */
    public short external_port 	= 0x00;		/* zewn?trzny port */
    public int image_size 		= 0x00;		/* maksymalny rozmiar grafiki w KB */
    public int unknown2 		= 0xbe;		/* char z C, 0xbe */
    public String description 	= null;		/* opis, nie musi wyst?pi? */
    public int time;						/* czas, nie musi wyst?pi? */
}

public interface GDefinitions {
	
	int msg_void 			= -1;		/* uzywany gdy bufor pusty */
	
	/* pakiety wysylane */
	int msg_welcome 	 	 = 0x0001;
	int msg_login 			 = 0x0015;
	int msg_new_status 		 = 0x0002;	/* zmiana statusu <--> */
	int msg_notify_first 	 = 0x000f;	/* pierwsze 400 wpisow / zmiana statusu usera <--> */
	int msg_notify_last 	 = 0x0010;	/* i ostatnie */
	int msg_list_empty 		 = 0x0012;	/* wysylamy gdy mamy pusta liste */
	int msg_add_notify 		 = 0x000d;
	int msg_remove_notify 	 = 0x000e;
	int msg_send_msg 		 = 0x000b;	/* wyslanie wiadomosci */
	int msg_ping 			 = 0x0008;	/* ping */
	int msg_userlist_request = 0x0016;  /* typ pakietu do zazadzania lista kontaktow import/eksport */
	
	/* pakiety odbierane */
	int msg_login_ok 		= 0x0003;
	int msg_login_failed 	= 0x0009;
	int msg_need_email 		= 0x0014;
	int msg_notify_reply 	= 0x000c;	/* lista kontaktow */
	int msg_notify_reply60 	= 0x0011;	/* lista kontaktow */
	int msg_status 			= 0x0002;	/* odpowiedz servera info o statusie usera */
	int msg_status60 		= 0x000f;	/* odpowiedz servera info o statusie usera (drugi typ) */
	int msg_recv_msg 		= 0x000a;	/* odebranie wiadomosci */
	int msg_recv_msg_list   = -1;		/* ARRAY LISTA PAKIETOW msg_recv_msg  */
	int msg_pong 			= 0x0007;	/* pong */
	int msg_disconnecting 	= 0x000b;	/* zerwanie polaczenia, polaczenie innego klienta na ten sam nr */
	int msg_userlist_reply 	= 0x0010; 	/* import listy */
	int msg_send_msg_ack	= 0x0005;	/* pakiet potwierdzajacy wyslanie wiadomosci */
	
	/* maski */
	int mask_has_audio_mask = 0x40000000;
	int mask_era_omnix_mask = 0x04000000;

	/* statusy */
	int status_not_avail 		= 0x0001;
	int status_not_avail_descr	= 0x0015;
	int status_avail 			= 0x0002;
	int status_avail_descr 		= 0x0004;
	int status_busy 			= 0x0003;
	int status_busy_descr 		= 0x0005;
	int status_invisible 		= 0x0014;
	int status_invisible_descr 	= 0x0016;
	int status_blocked 			= 0x0006;
	int status_friends_mask 	= 0x8000;
	
	/* typy userow */
	byte user_buddy 	= 0x01;
	byte user_friend 	= 0x02;
	byte user_blocked 	= 0x04;
	byte user_offline	= 0x01;
	byte user_normal 	= 0x03;
	
	/* typy wiadomosci */
	int msgclass_queued	= 0x0001;	/* Bit ustawiany wy??cznie przy odbiorze wiadomo?ci, gdy wiadomo?? zosta?a wcze?niej zakolejkowania z powodu nieobecno?ci */
	int msgclass_msg	= 0x0004;	/* wiadomosc w osobnym okienku */
	int msgclass_chat	= 0x0008;	/* wiadmosc czescia tocz?cej sie rozmowy i zostanie wy?wietlona w istniej?cym okienku */
	int msgclass_ctcp	= 0x0010;	/* Wiadomo?? jest przeznaczona dla klienta Gadu-Gadu i nie powinna by? wy?wietlona u?ytkownikowi. */
	int msgclass_ack	= 0x0020;	/* Klient nie ?yczy sobie potwierdzenia wiadomo?ci. */
	
	/* userlist */
	byte userlist_put		= 0x00;            /* pocz?tek eksportu listy */
	byte userlist_put_more 	= 0x01;		       /* dalsza cz??? eksportu listy */
	byte userlist_get 		= 0x02;            /* import listy */
	
}

