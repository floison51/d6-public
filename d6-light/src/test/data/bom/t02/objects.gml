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
		label	"2 Part:PART81"
		graphics
		[
			x	71.02598276289683
			y	110.0
			w	94.701171875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"2 Part:PART81"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	1
		label	"1 Part:PART80"
		graphics
		[
			x	71.02598276289683
			y	15.0
			w	94.701171875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"1 Part:PART80"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	2
		label	"4 Part:PART83"
		graphics
		[
			x	47.3505859375
			y	205.0
			w	94.701171875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"4 Part:PART83"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	3
		label	"3 Part:PART82"
		graphics
		[
			x	109.70137958829365
			y	250.0
			w	94.701171875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"3 Part:PART82"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	4
		label	"6 Document:DOC2"
		graphics
		[
			x	278.7505859375
			y	110.0
			w	115.373046875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"6 Document:DOC2"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	5
		label	"5 Document:DOC1"
		graphics
		[
			x	278.7505859375
			y	60.0
			w	115.373046875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"5 Document:DOC1"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	6
		label	"8 Document:DOC4"
		graphics
		[
			x	206.06368117559526
			y	205.0
			w	115.373046875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"8 Document:DOC4"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	7
		label	"7 Document:DOC3"
		graphics
		[
			x	206.06368117559526
			y	155.0
			w	115.373046875
			h	30.0
			type	"ellipse"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"7 Document:DOC3"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	edge
	[
		source	0
		target	3
		label	"10"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	71.02598276289683
					y	110.0
				]
				point
				[
					x	94.70127573164683
					y	170.5
				]
				point
				[
					x	109.70137958829365
					y	170.5
				]
				point
				[
					x	109.70137958829365
					y	250.0
				]
			]
		]
		edgeAnchor
		[
			xSource	0.5
			ySource	0.8645833333333334
			yTarget	-1.0
		]
		LabelGraphics
		[
			text	"10"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"tail"
		]
	]
	edge
	[
		source	0
		target	2
		label	"11"
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			xSource	-0.49999993145765476
			ySource	0.8645833333335062
			yTarget	-1.0
		]
		LabelGraphics
		[
			text	"11"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
	edge
	[
		source	5
		target	4
		label	"12"
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
			text	"12"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
	edge
	[
		source	1
		target	5
		label	"13"
		graphics
		[
			fill	"#000000"
			Line
			[
				point
				[
					x	71.02598276289683
					y	15.0
				]
				point
				[
					x	169.72022879464288
					y	15.0
				]
				point
				[
					x	169.72022879464288
					y	60.0
				]
				point
				[
					x	278.7505859375
					y	60.0
				]
			]
		]
		edgeAnchor
		[
			xSource	1.0
			xTarget	-1.0
		]
		LabelGraphics
		[
			text	"13"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
	edge
	[
		source	7
		target	6
		label	"14"
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
			text	"14"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
	edge
	[
		source	0
		target	7
		label	"15"
		graphics
		[
			fill	"#000000"
			Line
			[
				point
				[
					x	71.02598276289683
					y	110.0
				]
				point
				[
					x	133.3767764136905
					y	110.0
				]
				point
				[
					x	133.3767764136905
					y	155.0
				]
				point
				[
					x	206.06368117559526
					y	155.0
				]
			]
		]
		edgeAnchor
		[
			xSource	1.0
			xTarget	-1.0
		]
		LabelGraphics
		[
			text	"15"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"tail"
		]
	]
	edge
	[
		source	1
		target	0
		label	"9"
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
			text	"9"
			fontSize	12
			fontName	"Dialog"
			model	"six_pos"
			position	"head"
		]
	]
]
