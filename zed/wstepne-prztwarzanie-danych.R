download.file("http://www.cs.put.poznan.pl/dbrzezinski/teaching/zed/PortretPoznania2002-2014.csv", destfile = "PortretPoznania2002-2014.csv")

df <- read.csv("./PortretPoznania2002-2014.csv",
               header = FALSE, skip = 11,
               nrows = 15, col.names=strsplit(readLines("./PortretPoznania2002-2014.csv", n = 1), ",")[[1]])

summary(df)

hist(df$X2002)
