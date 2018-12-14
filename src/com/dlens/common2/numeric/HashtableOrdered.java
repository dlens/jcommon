
package com.dlens.common2.numeric;

import com.dlens.common2.exceptions.XMLFormatException;
import com.dlens.common2.interfaces.JDOMToObject;
import com.dlens.common2.interfaces.JDOMable;
import com.dlens.common2.interfaces.JDOMit;
import com.dlens.common2.interfaces.ObjectToJDOM;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.jdom.Element;

/**
 *
 * @param K The key type
 * @param V The value type
 * @author wjadams
 */
public class HashtableOrdered<K,V> implements java.io.Serializable {
    private Vector<K> keys=new Vector<K>();
    private Vector<V> values=new Vector<V>();
    private Hashtable<K,V> hash=new Hashtable<K,V>();
    
    public HashtableOrdered() {
    }
    
    public HashtableOrdered(Element elt, JDOMToObject keyJDOMer, JDOMToObject valueJDOMer)
        throws XMLFormatException 
    {
        fromElement(elt, keyJDOMer, valueJDOMer);
    }
    public HashtableOrdered(Element elt, JDOMToObject valueJDOMer)
        throws XMLFormatException 
    {
        fromElement(elt, valueJDOMer);
    }
    
    public HashtableOrdered(HashtableOrdered src) {
        keys=new Vector(src.keys);
        values=new Vector(src.values);
        hash=new Hashtable(src.hash);
    }
    public boolean containsKey(K key) {
        return hash.containsKey(key);
    }
    
    public V remove(K key) {
        int pos = keys.indexOf(key);
        if (pos < 0) return null;
        keys.remove(pos); values.remove(pos);
        return hash.remove(key);
    }
    
    public void resetKey(K oldKey, K newKey) {
        int pos = keys.indexOf(oldKey);
        if (pos < 0) return;
        V val = hash.remove(oldKey);
        hash.put(newKey, val);
        keys.set(pos, newKey);    
    }
    
    public void clear() {
        keys.clear();
        values.clear();
        hash.clear();
    }
    
    public int size() {
        return keys.size();
    }
    
    public void put(K key, V value) {
        if (!hash.containsKey(key)) {
            keys.add(key);
            values.add(value);
            hash.put(key, value);
        } else {
            int keyIndex = keys.indexOf(key);
            values.set(keyIndex, value);
            hash.put(key, value);
        }
    }
    
    public V get(K key) {
        return hash.get(key);
    }
    
    public K findKey(K key) {
        int keyIndex=keys.indexOf(key);
        if (keyIndex < 0)
            return null;
        return keys.get(keyIndex);
    }
    
    public K getKey(int i) {
        return keys.get(i);
    }
    
    public V getValue(int i) {
        return values.get(i);
    }
    
    public Vector<K> keys() {
        return new Vector<K>(keys);
    }
    
    public Vector<V> values() {
        return new Vector<V>(values);
    }
    
    public Iterator<K> keyIterator() {
        return keys.iterator();
    }
    
    public Iterator<V> valuesIterator() {
        return values.iterator();
    }
    
    public void moveAfter(int start, int after) {
        VectorMoving.vectorMoveAfter(keys, start, after);
        VectorMoving.vectorMoveAfter(values, start, after);
    }
    
    public int indexOf(K key) {
        return keys.indexOf(key);
    }
    
    public int indexOfValue(V value) {
        return values.indexOf(value);
    }
    
    public Element toElement(String name, ObjectToJDOM keyJDOMer, ObjectToJDOM valueJDOMer) {
        Element rval=new Element(name);
        rval.addContent(JDOMit.toElement(hash, "hash", keyJDOMer, valueJDOMer));
        rval.addContent(JDOMit.toElement(keys, "keys", keyJDOMer));
        return rval;
    }
    
    public Element toElement(String name, ObjectToJDOM valueJDOMer) {
        return toElement(name, ObjectToJDOM.stringToJDOM(), valueJDOMer);
    }
    
    public void fromElement(Element elt, JDOMToObject keyJDOMer, JDOMToObject valueJDOMer)
        throws XMLFormatException
    {
        hash=(Hashtable)JDOMit.fromElement((Hashtable)null, elt.getChild("hash"), keyJDOMer, valueJDOMer);
        keys=(Vector)JDOMit.fromElement((Vector)null, elt.getChild("keys"), keyJDOMer);
        valuesFromKeys();
    }
    
    public void fromElement(Element elt, JDOMToObject valueJDOMer)
        throws XMLFormatException
    {
        fromElement(elt, JDOMToObject.stringFromJDOM(), valueJDOMer);
    }
    private void valuesFromKeys() {
        values=new Vector();
        for(int i=0; i<keys.size(); i++) {
            values.add(i, hash.get(keys.get(i)));
        }
    }
    
    public Object[] keysArray(Object []type) {
        return keys.toArray(type);
    }
    
    public Object[] valuesArray(Object []type) {
        return values.toArray(type);
    }
    
    public K getKey(V val) {
        if (val==null) return null;
        for(int i=0; i<keys.size(); i++) {
            if (val.equals(values.get(i)))
                return keys.get(i);
        }
        return null;
    }
    
    public Enumeration<V> elements() {
        return new Enumeration<V> () {
            int count=0;
            public boolean hasMoreElements() {
                if (count < values.size()) {
                    return true;
                } else {
                    return false;
                }
            }

            public V nextElement() {
                V rval;
                if (count < values.size()) {
                    rval=values.get(count);
                    count++;
                    return rval;
                } else {
                    return null;
                }
            }
            
        };
    }
    
    public Element toElement(String name) {
        return toElement(name, ObjectToJDOM.jdomableToJDOM(),
                ObjectToJDOM.jdomableToJDOM());
    }
    
    /**
     * Assumes keyClass and valueClass implement JDOMable and have an
     * empty constructor.
     * @param elt
     * @param keyClass
     * @param valueClass
     * @throws com.dlens.common2.exceptions.XMLFormatException
     */
    public void fromElement(Element elt, Class keyClass, Class valueClass) throws XMLFormatException {
        fromElement(elt,
                JDOMToObject.jdomableFromJDOM(keyClass),
                JDOMToObject.jdomableFromJDOM(valueClass));
        
    }
    
    public static Hashtable reverse(Hashtable in) {
        if (in==null) return null;
        Enumeration keys=in.keys();
        Hashtable rval=new Hashtable();
        while (keys.hasMoreElements()) {
            Object key=keys.nextElement();
            Object val=in.get(key);
            if (val!=null)
                rval.put(val, key);
        }
        return rval;
    }

    public void setValue(int place, V aVal) {
        K key = keys.get(place);
        put(key, aVal);
    }

    /**
     * Removes all keys that have this as the corresponding value.
     * @param value
     */
	public void removeValue(V value, boolean useStrictEquals) {
		Object key, v;
		for(int i=(keys.size()-1); i >= 0; i--) {
			key = keys.get(i);
			v = values.get(i);
			if (equals(v, value, useStrictEquals)) {
				remove(i);
			}
		}
	}

	private void remove(int i) {
		if ((i < 0) || (i >= keys.size()))
			throw new ArrayIndexOutOfBoundsException();
		K key = keys.remove(i);
		values.remove(i);
		hash.remove(key);
	}

	private boolean equals(Object v1, Object v2, boolean useStrictEquals) {
		if (useStrictEquals) {
			return v1==v2;
		} else {
			if (v1==null)
				return v2==null;
			else
				return v1.equals(v2);
		}
	}

	public void put(int i, K clusterId, V movingCluster) {
		keys.add(i, clusterId);
		values.add(movingCluster);
	}
}
