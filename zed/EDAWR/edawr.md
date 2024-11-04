---
title: "Raport o zachorowaniach na gruźlicę"
author: "Dawid Stasiak"
date: "2024-11-04"
output:
  html_document:
    toc: true
    toc_float: true
    keep_md: true
---
## Biblioteki
Do przygotowania raportu wykorzystano biblioteki:

- EDAWR
- dplyr


## Wczytanie danych

``` r
tib_tb <- tibble::as_tibble(tb)
```

## Krótkie podsumowanie

``` r
head(tib_tb)
```

```
## # A tibble: 6 × 6
##   country      year sex    child adult elderly
##   <chr>       <int> <chr>  <int> <int>   <int>
## 1 Afghanistan  1995 female    NA    NA      NA
## 2 Afghanistan  1995 male      NA    NA      NA
## 3 Afghanistan  1996 female    NA    NA      NA
## 4 Afghanistan  1996 male      NA    NA      NA
## 5 Afghanistan  1997 female     5    96       1
## 6 Afghanistan  1997 male       0    26       0
```

``` r
str(tib_tb)
```

```
## tibble [3,800 × 6] (S3: tbl_df/tbl/data.frame)
##  $ country: chr [1:3800] "Afghanistan" "Afghanistan" "Afghanistan" "Afghanistan" ...
##  $ year   : int [1:3800] 1995 1995 1996 1996 1997 1997 1998 1998 1999 1999 ...
##  $ sex    : chr [1:3800] "female" "male" "female" "male" ...
##  $ child  : int [1:3800] NA NA NA NA 5 0 45 30 25 8 ...
##  $ adult  : int [1:3800] NA NA NA NA 96 26 1142 500 484 212 ...
##  $ elderly: int [1:3800] NA NA NA NA 1 0 20 41 8 8 ...
```

``` r
summary(tib_tb)
```

```
##    country               year          sex                child        
##  Length:3800        Min.   :1995   Length:3800        Min.   :    0.0  
##  Class :character   1st Qu.:1999   Class :character   1st Qu.:   25.0  
##  Mode  :character   Median :2004   Mode  :character   Median :   76.0  
##                     Mean   :2004                      Mean   :  493.2  
##                     3rd Qu.:2009                      3rd Qu.:  264.5  
##                     Max.   :2013                      Max.   :25661.0  
##                                                       NA's   :396      
##      adult           elderly        
##  Min.   :     0   Min.   :     0.0  
##  1st Qu.:  1128   1st Qu.:    84.5  
##  Median :  2589   Median :   230.0  
##  Mean   : 10864   Mean   :  1253.0  
##  3rd Qu.:  6706   3rd Qu.:   640.0  
##  Max.   :731540   Max.   :125991.0  
##  NA's   :413      NA's   :413
```
Wykorzystany zbiór danych zawiera informacje o zachorowaniach na gruźlicę w różnych krajach w latach 1995-2013.
W zbiorze jest 3800 wierszy i 6 kolumn. Kolumny to:

- country - kraj
- year - rok
- sex - płeć
- child - liczba zachorowań u dzieci
- adult - liczba zachorowań u dorosłych
- elderly - liczba zachorowań u osób starszych

## Liczba zachorowań z podziałem na płeć

```
## # A tibble: 2 × 2
##   sex       cases
##   <chr>     <int>
## 1 female 15610599
## 2 male   27016456
```

![](edawr_files/figure-html/cases_sex-1.png)<!-- -->

Z powyższego wykresu wynika, że liczba zachorowań u mężczyzn (około 27 mln) jest większa niż u kobiet (około 16 mln).

## Liczba zachorowań w kolejnych latach z podziałem na grupy wiekowe
![](edawr_files/figure-html/cases_year-1.png)<!-- -->

Z powyższego wykresu wynika, że liczba zachorowań u dorosłych jest największa, a liczba zachorowań u dzieci jest najmniejsza. W latach 2005-2009 liczba zachorowań u dorosłych gwałtownie wzrosła, a ostatnim roku (2012-2013) zaczęła spadać.

## Liczba zachorowań w kolejnych latach z podziałem na kraje i grupy wiekowe
![](edawr_files/figure-html/cases_year_country-1.png)<!-- -->![](edawr_files/figure-html/cases_year_country-2.png)<!-- -->![](edawr_files/figure-html/cases_year_country-3.png)<!-- -->![](edawr_files/figure-html/cases_year_country-4.png)<!-- -->![](edawr_files/figure-html/cases_year_country-5.png)<!-- -->![](edawr_files/figure-html/cases_year_country-6.png)<!-- -->![](edawr_files/figure-html/cases_year_country-7.png)<!-- -->![](edawr_files/figure-html/cases_year_country-8.png)<!-- -->![](edawr_files/figure-html/cases_year_country-9.png)<!-- -->![](edawr_files/figure-html/cases_year_country-10.png)<!-- -->![](edawr_files/figure-html/cases_year_country-11.png)<!-- -->![](edawr_files/figure-html/cases_year_country-12.png)<!-- -->![](edawr_files/figure-html/cases_year_country-13.png)<!-- -->![](edawr_files/figure-html/cases_year_country-14.png)<!-- -->![](edawr_files/figure-html/cases_year_country-15.png)<!-- -->![](edawr_files/figure-html/cases_year_country-16.png)<!-- -->![](edawr_files/figure-html/cases_year_country-17.png)<!-- -->![](edawr_files/figure-html/cases_year_country-18.png)<!-- -->![](edawr_files/figure-html/cases_year_country-19.png)<!-- -->![](edawr_files/figure-html/cases_year_country-20.png)<!-- -->![](edawr_files/figure-html/cases_year_country-21.png)<!-- -->![](edawr_files/figure-html/cases_year_country-22.png)<!-- -->![](edawr_files/figure-html/cases_year_country-23.png)<!-- -->![](edawr_files/figure-html/cases_year_country-24.png)<!-- -->![](edawr_files/figure-html/cases_year_country-25.png)<!-- -->![](edawr_files/figure-html/cases_year_country-26.png)<!-- -->![](edawr_files/figure-html/cases_year_country-27.png)<!-- -->![](edawr_files/figure-html/cases_year_country-28.png)<!-- -->![](edawr_files/figure-html/cases_year_country-29.png)<!-- -->![](edawr_files/figure-html/cases_year_country-30.png)<!-- -->![](edawr_files/figure-html/cases_year_country-31.png)<!-- -->![](edawr_files/figure-html/cases_year_country-32.png)<!-- -->![](edawr_files/figure-html/cases_year_country-33.png)<!-- -->![](edawr_files/figure-html/cases_year_country-34.png)<!-- -->![](edawr_files/figure-html/cases_year_country-35.png)<!-- -->![](edawr_files/figure-html/cases_year_country-36.png)<!-- -->![](edawr_files/figure-html/cases_year_country-37.png)<!-- -->![](edawr_files/figure-html/cases_year_country-38.png)<!-- -->![](edawr_files/figure-html/cases_year_country-39.png)<!-- -->![](edawr_files/figure-html/cases_year_country-40.png)<!-- -->![](edawr_files/figure-html/cases_year_country-41.png)<!-- -->![](edawr_files/figure-html/cases_year_country-42.png)<!-- -->![](edawr_files/figure-html/cases_year_country-43.png)<!-- -->![](edawr_files/figure-html/cases_year_country-44.png)<!-- -->![](edawr_files/figure-html/cases_year_country-45.png)<!-- -->![](edawr_files/figure-html/cases_year_country-46.png)<!-- -->![](edawr_files/figure-html/cases_year_country-47.png)<!-- -->![](edawr_files/figure-html/cases_year_country-48.png)<!-- -->![](edawr_files/figure-html/cases_year_country-49.png)<!-- -->![](edawr_files/figure-html/cases_year_country-50.png)<!-- -->![](edawr_files/figure-html/cases_year_country-51.png)<!-- -->![](edawr_files/figure-html/cases_year_country-52.png)<!-- -->![](edawr_files/figure-html/cases_year_country-53.png)<!-- -->![](edawr_files/figure-html/cases_year_country-54.png)<!-- -->![](edawr_files/figure-html/cases_year_country-55.png)<!-- -->![](edawr_files/figure-html/cases_year_country-56.png)<!-- -->![](edawr_files/figure-html/cases_year_country-57.png)<!-- -->![](edawr_files/figure-html/cases_year_country-58.png)<!-- -->![](edawr_files/figure-html/cases_year_country-59.png)<!-- -->![](edawr_files/figure-html/cases_year_country-60.png)<!-- -->![](edawr_files/figure-html/cases_year_country-61.png)<!-- -->![](edawr_files/figure-html/cases_year_country-62.png)<!-- -->![](edawr_files/figure-html/cases_year_country-63.png)<!-- -->![](edawr_files/figure-html/cases_year_country-64.png)<!-- -->![](edawr_files/figure-html/cases_year_country-65.png)<!-- -->![](edawr_files/figure-html/cases_year_country-66.png)<!-- -->![](edawr_files/figure-html/cases_year_country-67.png)<!-- -->![](edawr_files/figure-html/cases_year_country-68.png)<!-- -->![](edawr_files/figure-html/cases_year_country-69.png)<!-- -->![](edawr_files/figure-html/cases_year_country-70.png)<!-- -->![](edawr_files/figure-html/cases_year_country-71.png)<!-- -->![](edawr_files/figure-html/cases_year_country-72.png)<!-- -->![](edawr_files/figure-html/cases_year_country-73.png)<!-- -->![](edawr_files/figure-html/cases_year_country-74.png)<!-- -->![](edawr_files/figure-html/cases_year_country-75.png)<!-- -->![](edawr_files/figure-html/cases_year_country-76.png)<!-- -->![](edawr_files/figure-html/cases_year_country-77.png)<!-- -->![](edawr_files/figure-html/cases_year_country-78.png)<!-- -->![](edawr_files/figure-html/cases_year_country-79.png)<!-- -->![](edawr_files/figure-html/cases_year_country-80.png)<!-- -->![](edawr_files/figure-html/cases_year_country-81.png)<!-- -->![](edawr_files/figure-html/cases_year_country-82.png)<!-- -->![](edawr_files/figure-html/cases_year_country-83.png)<!-- -->![](edawr_files/figure-html/cases_year_country-84.png)<!-- -->![](edawr_files/figure-html/cases_year_country-85.png)<!-- -->![](edawr_files/figure-html/cases_year_country-86.png)<!-- -->![](edawr_files/figure-html/cases_year_country-87.png)<!-- -->![](edawr_files/figure-html/cases_year_country-88.png)<!-- -->![](edawr_files/figure-html/cases_year_country-89.png)<!-- -->![](edawr_files/figure-html/cases_year_country-90.png)<!-- -->![](edawr_files/figure-html/cases_year_country-91.png)<!-- -->![](edawr_files/figure-html/cases_year_country-92.png)<!-- -->![](edawr_files/figure-html/cases_year_country-93.png)<!-- -->![](edawr_files/figure-html/cases_year_country-94.png)<!-- -->![](edawr_files/figure-html/cases_year_country-95.png)<!-- -->![](edawr_files/figure-html/cases_year_country-96.png)<!-- -->![](edawr_files/figure-html/cases_year_country-97.png)<!-- -->![](edawr_files/figure-html/cases_year_country-98.png)<!-- -->![](edawr_files/figure-html/cases_year_country-99.png)<!-- -->![](edawr_files/figure-html/cases_year_country-100.png)<!-- -->
