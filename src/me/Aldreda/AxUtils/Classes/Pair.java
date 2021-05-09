package me.Aldreda.AxUtils.Classes;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class Pair<F,S> {
	private final F first;
	private final S second;
	
	protected Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public F first() {
		return this.first;
	}
	
	public S second() {
		return this.second;
	}
	
	public Pair<S,F> swap() {
		return of(this.second,this.first);
	}
	
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) return false;
		Pair<?,?> other = (Pair<?,?>) obj;
		return Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second);
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(new Object[]{this.first,this.second});
	}
	
	public <F2> Pair<F2,S> mapFirst(Function<? super F,? extends F2> function) {
		return of(function.apply(this.first),this.second);
	}
	
	public <S2> Pair<F,S2> mapSecond(Function<? super S,? extends S2> function) {
		return of(this.first,function.apply(this.second));
	}
	
	public static <F,S> Pair<F,S> of(F first, S second) {
		return new Pair<F,S>(first,second);
	}
	
	public static <F,S> Map<F,S> toMap(Pair<F,S> ... pairs) {
		Map<F,S> map = new HashMap<F,S>();
		for (Pair<F,S> pair : pairs) map.put(pair.first,pair.second);
		return map;
	}
	
	public static <F,S> Pair<F,S>[] fromMap(Map<F,S> map) {
		Pair<F,S>[] arr = (Pair<F,S>[]) Array.newInstance(Pair.class,map.size());
		int i = 0;
		for (Entry<F,S> entry : map.entrySet()) arr[i++] = (of(entry.getKey(),entry.getValue()));
		return arr;
	}
}