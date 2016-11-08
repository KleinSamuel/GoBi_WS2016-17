#!/home/proj/biosoft/software/R/R-3.3.0/bin/Rscript

args = commandArgs(TRUE)

x <- read.csv(file = args[0], head = FALSE, sep = "\t", nrows = 1)
y <- read.csv(file = args[0], head = FALSE, sep = "\t", nrows = 1, skip = 1)

z <- cumsum(y)

png(args[1])

plot(x, z, type = "l")

dev.off()