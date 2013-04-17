BlockLayout
==========
Swing LayoutManager2 implementation that tries to mimic the display block/inline system from CSS.

For block components, preferred width will be respected, unless the container is larger, in which case it will stretch to fill.
For inline components, preferred width will always be respected. Caveat: if an inline component's preferred width is larger than the largest block element's width and larger than the container's size, the rest will be cut off.
For both, the preferred height will always be respected.

Example usage:
	JPanel panel = new JPanel();
	panel.setLayout(new BlockLayout());

	// these will all appear on the same line, if the container is large enough - otherwise some may wrap
	panel.add(new JLabel("Oh"), BlockLayout.INLINE);
	panel.add(new JLabel("hello"), BlockLayout.INLINE);
	panel.add(new JLabel("there"), BlockLayout.INLINE);

	// this will appear on a new line
	panel.add(new JLabel("!"), BlockLayout.BLOCK);

This is a very simple implementation - future ideas including specifying padding / margin on components, and specifying vertical alignment for inline components.
