package jshm.internal.concepts;

import jshm.util.TestTimer;

/*
 * instanceof definately seems faster than using a type field
 */
public class EnumVsTypeField {
	public static void main(String[] args) {
		int n = 200000000;
		Parent o = new B();
		
		System.out.println("instanceof");
		TestTimer.start(false);
		for (int i = 0; i < n; i++) {
			if (o instanceof A) { a(); }
			else if (o instanceof B) { b(); }
			else assert false;
		}
		TestTimer.stop(false);
		
		System.out.println("getClass()");
		TestTimer.start(false);
		Class<?> c = o.getClass();
		for (int i = 0; i < n; i++) {
			if (c == A.class) { a(); }
			else if (c == B.class) { b(); }
			else assert false;
		}
		TestTimer.stop(false);
		
		System.out.println("overridden method");
		TestTimer.start(false);
		for (int i = 0; i < n; i++) {
			o.action();
		}
		TestTimer.stop(false);
		
		System.out.println("type switch");
		TestTimer.start(false);
		Type t = o.getType();
		for (int i = 0; i < n; i++) {
			switch (t) {
				case A: a(); break;
				case B: b(); break;
				default: assert false;
			}
		}
		TestTimer.stop(false);
	}
	
	private static void a() {}
	private static void b() {}
	
	private static enum Type {
		A, B;
	}
	
	private static abstract class Parent {
		public Parent() {}
		public abstract Type getType();
		public abstract void action();
	}
	
	private static final class A extends Parent {
		public A() {}
		public final Type getType() {
			return Type.A;
		}
		
		public final void action() { a(); }
	}
	
	private static final class B extends Parent {
		public B() {}
		public final Type getType() {
			return Type.B;
		}
		
		public final void action() { b(); }
	}
}
