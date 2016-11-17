#!/home/proj/biosoft/software/R/R-3.3.0/bin/Rscript
# args[1] = input, args[2] = outputFile, args[3] = biotypePair

args = commandArgs(TRUE)
biotype = args[3]
#data = read.table(args[1], header = FALSE, fill = TRUE)
#data[is.na(data)] = 0
#data = data[,-1]
same = as.numeric(scan(args[1], nlines = 1, blank.lines.skip = TRUE, what = "int"))
sameVal = cumsum(as.numeric(scan(args[1], nlines = 1, blank.lines.skip = TRUE, what = "int", skip = 1)))
if(length(same)>1){
same = same[-1]
sameVal = sameVal[-1]
}

diff = as.numeric(scan(args[1], nlines = 1, blank.lines.skip = TRUE, what = "int", skip = 3))
diffVal = cumsum(as.numeric(scan(args[1], nlines = 1, blank.lines.skip = TRUE, what = "int", skip = 4)))
if(length(diff)>1){
diff = diff[-1]
diffVal = diffVal[-1]
}

disregard = as.numeric(scan(args[1], nlines = 1, blank.lines.skip = TRUE, what = "int", skip = 6))
disregardVal = cumsum(as.numeric(scan(args[1], nlines = 1, blank.lines.skip = TRUE, what = "int", skip = 7)))
if(length(disregard)>1){
disregard = disregard[-1]
disregardVal = disregardVal[-1]
}

maxX = max(same, diff, disregard)
maxY = max(sameVal, diffVal, disregardVal)

png(args[2])

plot(same, sameVal, type = "l", xlim = c(0, maxX), ylim = c(0, maxY), col = "red", main=biotype, xlab = "#overlapping genes", ylab = "#genes with x overlapping genes(cumulative)")
lines(diff, diffVal, col = "blue")
lines(disregard, disregardVal, col = "black")
legend("topleft", c("sameStrand", "differentStrand", "disregardingStrand"), lty=c(1,1,1), col = c("red","blue","black"))

dev.off()