#!/home/proj/biosoft/software/R/R-3.3.0/bin/Rscript
# 

args = commandArgs(TRUE)
biotype = args[3]
data = read.table(args[1], header = FALSE, fill = TRUE)
data[is.na(data)] = 0
data = data[,-1]
same = as.numeric(data[1,])
sameVal = cumsum(as.numeric(data[2,]))
diff = as.numeric(data[3,])
diffVal = cumsum(as.numeric(data[4,]))
disregard = as.numeric(data[5,])
disregardVal = cumsum(as.numeric(data[6,]))
maxX = max(same, diff, disregard)
maxY = max(sameVal, diffVal, disregardVal)

jpeg(args[2])

plot(same, sameVal, type = "l", xlim = c(0, maxX), ylim = c(0, maxY), col = "red", main=biotype, xlab = "#overlapping genes", ylab = "#genes with x overlapping genes(cumulative)")
lines(diff, diffVal, col = "blue")
lines(disregard, disregardVal, col = "black")
legend("topleft", c("sameStrand", "differentStrand", "disregardingStrand"), lty=c(1,1,1), col = c("red","blue","black"))

dev.off()