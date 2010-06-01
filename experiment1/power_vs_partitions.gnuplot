set terminal pdf

set title "Face detection and recognition power estimates versus different partition schemes"
#set auto x
#set yrange [0:300000]

set style data histogram
set style histogram rowstacked gap 4 title offset 2,1
set style fill solid border -1

#set boxwidth 1

set key noinvert left

set xtics nomirror rotate by -45

set xlabel "Image size" offset 0,-2
set ylabel "Power, mW"

plot \
newhistogram "400x300",\
	    '< cat total.txt | grep 400x300' using 7:xtic(2) title "LCD" lt 1,\
									  '' using 6 title "CPU" lt 2,\
									  '' using 8 title "Wifi" lt 3,\
									  '' using 9 title "3G" lt 4,\
newhistogram "800x600",\
	    '< cat total.txt | grep 800x600' using 7:xtic(2) notitle lt 1,\
									  '' using 6 notitle lt 2,\
									  '' using 8 notitle lt 3,\
									  '' using 9 notitle lt 4,\
newhistogram "1071x800",\
	    '< cat total.txt | grep 1071x800' using 7:xtic(2) notitle lt 1,\
									  '' using 6 notitle lt 2,\
									  '' using 8 notitle lt 3,\
									  '' using 9 notitle lt 4


#	newhistogram "400x300"

# '< cat total.txt | grep 400x300' using 6:xtics(2) title "CPU",\
#									  '' using 7 title "LCD",\
#									  '' using 8 title "Wifi",\
#									  '' using 9 title "3G" 
#
#	newhistogram "800x600"
#
#	newhistogram "1071x800"
#
#'< cat total.txt | grep FullLocal' using ($3/1000):xtic(1) title "Full Local", \
#	 '< cat total.txt | grep Local3G'   using ($3/1000):xtic(1) title "Local detector / remote recognizer (3G)", \
#	 '< cat total.txt | grep LocalWifi' using ($3/1000):xtic(1) title "Local detector / remote recognizer (Wifi)", \
#	 '< cat total.txt | grep Full3G'    using ($3/1000):xtic(1) title "Full 3G", \
#	 '< cat total.txt | grep FullWifi'  using ($3/1000):xtic(1) title "Full Wifi"
#
##, '' u 12 ti col, '' u 13 ti col, '' u 14 ti col
##
#
