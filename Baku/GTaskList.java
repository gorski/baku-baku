package Baku;

import java.util.ArrayList;
import java.util.LinkedList;

import GMessage.GMessage;

public class GTaskList extends LinkedList {
	
	synchronized public void addTask(GMessage m) {
		super.addFirst(m);
	}
	
	synchronized public GMessage removeTask() {
		return (GMessage) super.removeLast();
	}
	
	synchronized public boolean isEmpty() {
		return super.isEmpty();
	}
}
