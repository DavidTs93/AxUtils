package me.Aldreda.AxUtils.Classes;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class Gradient {
	List<TextColor> nums = new ArrayList<TextColor>();
	
	public Gradient(int ... nums) {
		for (int num : nums) this.nums.add(TextColor.color(num));
		if (this.nums.isEmpty()) throw new IllegalArgumentException("Cannot initiate with no values!");
	}
	
	public Gradient(TextColor... colors) {
		for (TextColor color : colors) if (color != null) this.nums.add(color);
		if (this.nums.isEmpty()) throw new IllegalArgumentException("Cannot initiate with no values!");
	}
	
	public Gradient(Color... colors) {
		for (Color color : colors) if (color != null) this.nums.add(TextColor.color(color.asRGB()));
		if (this.nums.isEmpty()) throw new IllegalArgumentException("Cannot initiate with no values!");
	}
	
	public List<Integer> getGradientInts(int num) {
		List<Integer> gradient = new ArrayList<Integer>();
		for (TextColor color : getGradientTextColors(num)) gradient.add(color.value());
		return gradient;
	}
	
	public List<TextColor> getGradientTextColors(int num) {
		List<TextColor> gradient = new ArrayList<TextColor>();
		if (num == 0);
		else if (num == 1) gradient.add(nums.get(0));
		else if (num == 2) {
			gradient.add(nums.get(0));
			gradient.add(nums.get(nums.size() - 1));
		} else {
			TextColor current;
			TextColor next;
			current = num > 0 ? nums.get(0) : nums.get(nums.size() - 1);
			for (int i = 1; i < nums.size() - 1; i++) {
				int idx = num > 0 ? i : nums.size() - 1 - i;
				next = nums.get(idx);
				gradient.addAll(makeGradient(current,next,num));
				current = next;
			}
			gradient.add(num > 0 ? nums.get(nums.size() - 1) : nums.get(0));
		}
		return gradient;
	}
	
	private List<TextColor> makeGradient(TextColor start, TextColor end, int num) {
		List<TextColor> gradient = new ArrayList<TextColor>();
		for (int i = 0; i < num; i++) {
			float ratio = (float) i / (float) num;
			int red = Math.round(end.red() * ratio + start.red() * (1 - ratio));
			int green = Math.round(end.green() * ratio + start.green() * (1 - ratio));
			int blue = Math.round(end.blue() * ratio + start.blue() * (1 - ratio));
			gradient.add(TextColor.color(red,green,blue));
		}
		return gradient;
	}
	
	public List<Color> getGradientColors(int num) {
		List<Color> gradient = new ArrayList<Color>();
		for (TextColor color : getGradientTextColors(num)) gradient.add(Color.fromRGB(color.red(),color.green(),color.blue()));
		return gradient;
	}
}