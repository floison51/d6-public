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
		label	"A"
		graphics
		[
			x	20.67421564980159
			y	15.0
			w	20.673828125
			h	30.0
			type	"rectangle"
			raisedBorder	0
			fill	"#FFCC00"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"1"
			fontSize	12
			fontName	"Dialog"
			model	"null"
		]
		LabelGraphics
		[
		]
	]
	node
	[
		id	1
		label	"B"
		graphics
		[
			x	15.50556485615079
			y	76.0
			w	20.673828125
			h	30.0
			type	"rectangle"
			raisedBorder	0
			fill	"#FFCC00"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"2"
			fontSize	12
			fontName	"Dialog"
			model	"null"
		]
		LabelGraphics
		[
		]
	]
	node
	[
		id	2
		label	"C"
		graphics
		[
			x	10.3369140625
			y	137.0
			w	20.673828125
			h	30.0
			type	"rectangle"
			raisedBorder	0
			fill	"#FFCC00"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"3"
			fontSize	12
			fontName	"Dialog"
			model	"null"
		]
	]
	node
	[
		id	3
		label	"D"
		graphics
		[
			x	61.01112041170635
			y	137.0
			w	20.673828125
			h	30.0
			type	"rectangle"
			raisedBorder	0
			fill	"#FFCC00"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"4"
			fontSize	12
			fontName	"Dialog"
			model	"null"
		]
	]
	node
	[
		id	4
		label	"E"
		graphics
		[
			x	66.17977120535713
			y	76.0
			w	20.673828125
			h	30.0
			type	"rectangle"
			raisedBorder	0
			fill	"#FFCC00"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"5"
			fontSize	12
			fontName	"Dialog"
			model	"null"
		]
	]
	edge
	[
		source	0
		target	1
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			xSource	-0.5
			ySource	1.0
			yTarget	-1.0
		]
	]
	edge
	[
		source	1
		target	2
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			xSource	-0.5
			ySource	1.0
			yTarget	-1.0
		]
	]
	edge
	[
		source	1
		target	3
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	15.50556485615079
					y	76.0
				]
				point
				[
					x	20.67402188740079
					y	106.5
				]
				point
				[
					x	61.01112041170635
					y	106.5
				]
				point
				[
					x	61.01112041170635
					y	137.0
				]
			]
		]
		edgeAnchor
		[
			xSource	0.5
			ySource	1.0
			yTarget	-1.0
		]
	]
	edge
	[
		source	0
		target	4
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	20.67421564980159
					y	15.0
				]
				point
				[
					x	25.84267268105159
					y	45.5
				]
				point
				[
					x	66.17977120535713
					y	45.5
				]
				point
				[
					x	66.17977120535713
					y	76.0
				]
			]
		]
		edgeAnchor
		[
			xSource	0.5
			ySource	1.0
			yTarget	-1.0
		]
	]
]
