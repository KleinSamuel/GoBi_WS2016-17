#!/home/proj/biosoft/software/R/R-3.3.0/bin/Rscript

args = commandArgs(TRUE)
print(args[2])
data <- matrix(scan(args[1]), ncol = 2)

data[,2] <- cumsum(data[,2])
print(data)

png(args[2])

plot(data, type = "l", xlab = "proportion", ylab = "#occurences of certain proportion", main = "cumulative distribution of longest transcripts length\ndivided by union transcript length")

dev.off()
