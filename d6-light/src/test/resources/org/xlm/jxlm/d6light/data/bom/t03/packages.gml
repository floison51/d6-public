Creator	"yFiles"
Version	"2.20"
graph
[
	hierarchic	1
	label	""
	directed	1
	node
	[
		id	0
		label	"TL 11
1O 0L
( component )"
		graphics
		[
			x	141.02607576884918
			y	196.2587890625
			w	88.033203125
			h	58.103515625
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"TL 11
1O 0L
( component )"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	1
		label	"BOM 13
Part:PART1
3O 2L"
		graphics
		[
			x	44.3447265625
			y	107.1552734375
			w	78.01953125
			h	58.103515625
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"BOM 13
Part:PART1
3O 2L"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	2
		label	"BOM 14
Part:PART1-1
1O 0L"
		graphics
		[
			x	44.3447265625
			y	29.0517578125
			w	88.689453125
			h	58.103515625
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"BOM 14
Part:PART1-1
1O 0L"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	3
		label	"BOM 15
Part:PART1-3
1O 0L"
		graphics
		[
			x	163.03440910218256
			y	29.0517578125
			w	88.689453125
			h	58.103515625
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"BOM 15
Part:PART1-3
1O 0L"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	edge
	[
		source	1
		target	0
		label	"1L (17)"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	44.3447265625
					y	107.1552734375
				]
				point
				[
					x	44.3447265625
					y	151.70703125
				]
				point
				[
					x	119.0177749875992
					y	151.70703125
				]
				point
				[
					x	141.02607576884918
					y	196.2587890625
				]
			]
		]
		edgeAnchor
		[
			ySource	1.0
			xTarget	-0.5
			yTarget	-1.0
		]
		LabelGraphics
		[
			text	"1L (17)"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"tail"
		]
	]
	edge
	[
		source	2
		target	1
		label	"1L (18)"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			ySource	1.0
			yTarget	-1.0
		]
		LabelGraphics
		[
			text	"1L (18)"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
	edge
	[
		source	3
		target	0
		label	"1L (19)"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			ySource	1.0
			xTarget	0.5
			yTarget	-1.0
		]
		LabelGraphics
		[
			text	"1L (19)"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
]
