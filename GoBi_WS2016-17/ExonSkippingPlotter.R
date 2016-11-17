#!/home/proj/biosoft/software/R/R-3.3.0/bin/Rscript
# args[1] inputfile, args[2] = outputfolder, args[3] = gtfName

args = commandArgs(TRUE)

data = read.table(args[1], sep = "\t", header = TRUE)
maxSkippedExons = sort(as.numeric(data[,12]))
occurencesMaxSkippedExon = t(data.frame(table(maxSkippedExons)))
occurencesMaxSkippedExon[2,] = cumsum(occurencesMaxSkippedExon[2,])

png(paste0(args[2], "/", args[3], "_skipped_exons.png"))

plot(occurencesMaxSkippedExon[1,], occurencesMaxSkippedExon[2,], type = "l", main = "maximimum skipped exon distribution", xlab = "number of maximum skipped exons", ylab = "cumulative number of exonskippingevents")

dev.off()

maxSkippedBases = sort(as.numeric(data[,14]))
occurencesMaxSkippedBases = t(data.frame(table(maxSkippedBases)))
occurencesMaxSkippedBases[2,] = cumsum(occurencesMaxSkippedBases[2,])

png(paste0(args[2], "/", args[3], "_skipped_bases.png"))

plot(occurencesMaxSkippedBases[1,], occurencesMaxSkippedBases[2,], type = "l", main = "maximimum skipped bases distribution", xlab = "number of maximum skipped bases", ylab = "cumulative number of exonskippingevents")

dev.off()