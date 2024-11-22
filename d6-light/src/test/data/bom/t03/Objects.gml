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
		label	"2 Part:PART2
 BOM13"
		graphics
		[
			x	145.36962425595237
			y	96.103515625
			w	88.02734375
			h	43.40234375
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"2 Part:PART2
 BOM13"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	1
		label	"1 Part:PART1
 BOM13"
		graphics
		[
			x	44.013671875
			y	21.701171875
			w	88.02734375
			h	43.40234375
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"1 Part:PART1
 BOM13"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	2
		label	"4 Part:PART4
 TL11"
		graphics
		[
			x	274.06724330357144
			y	170.505859375
			w	88.02734375
			h	43.40234375
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"4 Part:PART4
 TL11"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	3
		label	"3 Part:PART3
 BOM13"
		graphics
		[
			x	123.36287822420636
			y	170.505859375
			w	88.02734375
			h	43.40234375
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"3 Part:PART3
 BOM13"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	4
		label	"6 Part:PART1-3
 BOM15"
		graphics
		[
			x	296.0739893353175
			y	21.701171875
			w	98.697265625
			h	43.40234375
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"6 Part:PART1-3
 BOM15"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	5
		label	"5 Part:PART1-1
 BOM14"
		graphics
		[
			x	167.37637028769842
			y	21.701171875
			w	98.697265625
			h	43.40234375
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"5 Part:PART1-1
 BOM14"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	edge
	[
		source	5
		target	0
		label	"10
 TLD18"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			ySource	1.0
			xTarget	0.5000001915010512
			yTarget	-0.8660786607871712
		]
		LabelGraphics
		[
			text	"10
 TLD18"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"stail"
		]
	]
	edge
	[
		source	4
		target	2
		label	"11
 TLD19"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			ySource	1.0
			xTarget	0.5000000563225863
			yTarget	-0.8660786607866565
		]
		LabelGraphics
		[
			text	"11
 TLD19"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
	edge
	[
		source	1
		target	0
		label	"7
 BOM13"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	44.013671875
					y	21.701171875
				]
				point
				[
					x	44.013671875
					y	58.90234375
				]
				point
				[
					x	123.36278831845237
					y	58.90234375
				]
				point
				[
					x	145.36962425595237
					y	96.103515625
				]
			]
		]
		edgeAnchor
		[
			ySource	1.0
			xTarget	-0.5
			yTarget	-0.8660786607866079
		]
		LabelGraphics
		[
			text	"7
 BOM13"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"tail"
		]
	]
	edge
	[
		source	0
		target	3
		label	"8
 BOM13"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			xSource	-0.5000001915010513
			ySource	0.8660786607871712
			yTarget	-1.0
		]
		LabelGraphics
		[
			text	"8
 BOM13"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"shead"
		]
	]
	edge
	[
		source	0
		target	2
		label	"9
 TLD17"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	145.36962425595237
					y	96.103515625
				]
				point
				[
					x	167.37646019345237
					y	133.3046875
				]
				point
				[
					x	252.06040736607142
					y	133.3046875
				]
				point
				[
					x	274.06724330357144
					y	170.505859375
				]
			]
		]
		edgeAnchor
		[
			xSource	0.5
			ySource	0.8660786607866079
			xTarget	-0.5
			yTarget	-0.8660786607866079
		]
		LabelGraphics
		[
			text	"9
 TLD17"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"tail"
		]
	]
]
