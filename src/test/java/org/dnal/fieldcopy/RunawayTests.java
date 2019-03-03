package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class RunawayTests {
	
	public static class Node {
		private String name;
		private Node next;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Node getNext() {
			return next;
		}
		public void setNext(Node next) {
			this.next = next;
		}
		
	}
	
	@Test
	public void testOK() {
		Node node2 = new Node();
		node2.setName("node2");
		Node node1 = new Node();
		node1.setName("node1");
		node1.setNext(node2);

		FieldCopier copier = createCopier();
		FieldCopyMapping mapping = copier.createMapping(Node.class, Node.class).autoCopy().build();
		
		Node nodeDest = new Node();
		copier.getOptions().logEachCopy = true;
		copier.copy(node1, nodeDest).withMappings(mapping).autoCopy().execute();
		assertEquals("node1", nodeDest.getName());
		assertEquals("node2", nodeDest.getNext().getName());
	}
	
	@Test(expected=FieldCopyException.class)
	public void testRunaway() {
		Node node2 = new Node();
		node2.setName("node2");
		Node node1 = new Node();
		node1.setName("node1");
		node1.setNext(node2);
		node2.setNext(node1); //self-reference
		
		//TO-DO - do we need to add detection of circular references?
		//https://stackoverflow.com/questions/47473716/entity-to-bean-with-cyclic-reference-in-java
		//Apache BeanUtils does not support circular references
		
		FieldCopier copier = createCopier();
		FieldCopyMapping mapping = copier.createMapping(Node.class, Node.class).autoCopy().build();
		
		Node nodeDest = new Node();
		copier.getOptions().logEachCopy = true;
		copier.copy(node1, nodeDest).withMappings(mapping).autoCopy().execute();
		assertEquals("node1", nodeDest.getName());
		assertEquals("node2", nodeDest.getNext().getName());
	}
	
	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
}
