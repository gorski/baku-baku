package GMessage;

import java.util.*;

import GConnection.GDefinitions;
import GConnection.GStatus;
import GConnection.ListEntry;


public class GMessage implements GDefinitions {
	public int gtype= msg_void;
	
	public String toString() {
		return "MESSAGE: "+gtype;
	}
}
