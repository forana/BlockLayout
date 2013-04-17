/*
 * 2013 Alex Foran
 */
package com.forana.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.LinkedList;
import java.util.List;

/**
 * Swing LayoutManager2 implementation that tries to mimic the display block/inline system from CSS.
 * 
 * For block components, preferred width will be respected, unless the container is larger, in which
 * case it will stretch to fill.
 * For inline components, preferred width will always be respected. Caveat: if an inline component's
 * preferred width is larger than the largest block element's width and larger than the container's
 * size, the rest will be cut off.
 * For both, the preferred height will always be respected.
 */
public class BlockLayout implements LayoutManager2 {
	/** Constant for inline display behavior.
	 * @see java.awt.Container.add(Component, Object) **/
	public static final Integer INLINE = 1;
	/** Constant for block display behavior.
	 * @see java.awt.Container.add(Component, Object) **/
	public static final Integer BLOCK = 2;
	
	private List<StyledComponent> components;
	private int renderHeight;
	private int renderWidth;
	private int minimumWidth;
	
	public BlockLayout() {
		this.components = new LinkedList<StyledComponent>();
		this.renderHeight = 0;
		this.renderWidth = 0;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		this.addLayoutComponent(comp, null);
	}
	
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		this.components.add(new StyledComponent(comp,
			constraints == INLINE ? INLINE : BLOCK));
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		this.components.remove(comp);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(this.renderWidth, this.renderHeight);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(this.minimumWidth, this.renderHeight);
	}

	@Override
	public void layoutContainer(Container parent) {
		int x = 0;
		int y = 0;
		int currentLineHeight = 0;
		for (StyledComponent component : this.components) {
			Component raw = component.component;
			if (component.style == BLOCK) {
				if (x != 0) {
					y += currentLineHeight;
				}
				raw.setBounds(0, y, this.renderWidth, raw.getPreferredSize().height);
				x = 0;
				y += raw.getPreferredSize().height;
				currentLineHeight = 0;
			} else {
				currentLineHeight = Math.max(currentLineHeight, raw.getPreferredSize().height);
				if (x + raw.getPreferredSize().width > this.renderWidth) {
					y += currentLineHeight;
					raw.setBounds(0, y, x==0 ? this.renderWidth : raw.getPreferredSize().width, raw.getPreferredSize().height);
					x = raw.getPreferredSize().width;
					currentLineHeight = raw.getPreferredSize().height;
				} else {
					raw.setBounds(x, y, raw.getPreferredSize().width, raw.getPreferredSize().height);
					x += component.component.getPreferredSize().width;
				}
			}
		}
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(0, 0);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		System.out.println("getLayoutAlignmentX - " + target);
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		System.out.println("getLayoutAlignmentY - " + target);
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		// get the max of all block elements' widths
		int maxWidth = 0;
		for (StyledComponent component : this.components) {
			if (component.style == BLOCK && component.component.getPreferredSize() != null) {
				maxWidth = Math.max(maxWidth, component.component.getPreferredSize().width);
			}
		}
		
		this.minimumWidth = maxWidth;
		this.renderWidth = Math.max(target.getWidth(), maxWidth);
		
		int x = 0;
		int y = 0;
		int currentLineHeight = 0;
		for (StyledComponent component : this.components) {
			if (component.style == BLOCK) {
				if (x != 0) {
					y += currentLineHeight;
				}
				x = 0;
				y += component.component.getPreferredSize().height;
				currentLineHeight = 0;
			} else {
				currentLineHeight = Math.max(currentLineHeight, component.component.getPreferredSize().height);
				if (x + component.component.getPreferredSize().width > this.renderWidth) {
					x = 0;
					y += currentLineHeight;
					currentLineHeight = 0;
				} else {
					x += component.component.getPreferredSize().width;
				}
			}
		}
		
		this.renderHeight = y + currentLineHeight;
	}
	
	/**
	 * Inner class for component/style tuple.
	 */
	private class StyledComponent {
		public final Component component;
		public final Integer style;
		
		public StyledComponent(Component component, Integer style) {
			this.component = component;
			this.style = style;
		}
		
		@Override
		public int hashCode() {
			return this.component.hashCode();
		}
		
		@Override
		public boolean equals(Object other) {
			return this.component.equals(other);
		}
	}
}
