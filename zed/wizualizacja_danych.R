library(ggplot2)
ggplot(
  data = diamonds,
  aes(x = depth, fill = cut)
) +
  geom_histogram(binwidth = 0.2, position = "dodge") +
  #facet_wrap(~ cut) +
  scale_x_continuous(limits = c(55, 70))

ggplot(
  data = diamonds,
  aes(x = price)#, color = cut)
) +
  geom_histogram(binwidth = 1000) +
  facet_wrap(~ cut) +
  scale_x_continuous(limits = c(0, 20000))

ggplot(
  data = diamonds,
  aes(x = carat, y = price)
) +
  geom_line(stat = "summary", fun = "mean") +
  geom_ribbon(
    aes(ymin = ..ymin.., ymax = ..ymax..),
    stat = "summary", fun.data = "mean_cl_normal"
  )

data <- read.csv("./pokemons.csv")
View(data)

ggplot(
  data = data,
  aes(x = type, y = attack)
) +
  geom_boxplot()


t <- ggplot(mpg, aes(cty, hwy)) + geom_point()
t + annotate(geom = "text", x = 8, y = 9, label = "A") 

ggplot(diamonds, aes(x = carat, y = price)) +
  # Add ribbon for min/max range
  stat_summary_bin(
    fun.min = min,
    fun.max = max,
    geom = "ribbon",
    alpha = 0.2,
    fill = "blue",
    binwidth = 0.1  # Adjust bin width as needed
  ) +
  # Add line for mean
  stat_summary_bin(
    fun = mean,
    geom = "line",
    color = "darkblue",
    size = 1,
    binwidth = 0.1
  ) +
  # Add points for mean values
  stat_summary_bin(
    fun = mean,
    geom = "point",
    color = "darkblue",
    size = 3,
    binwidth = 0.1
  )