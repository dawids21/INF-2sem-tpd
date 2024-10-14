-- cw 1
-- zad a
create table a6_lrs
(
    geom sdo_geometry
);

-- zad b
insert into a6_lrs
select geom
from streets_and_railroads sar
where sar.id = (with koszalin_geom as (select geom
                                       from major_cities
                                       where city_name = 'Koszalin')
                select sar1.id
                from streets_and_railroads sar1,
                     koszalin_geom kg
                where sdo_relate(sar1.geom,
                                 sdo_geom.sdo_buffer(kg.geom, 10, 1, 'unit=km'),
                                 'MASK=ANYINTERACT') = 'TRUE');

-- zad c
select sdo_geom.sdo_length(geom, 1, 'unit=km') as distance,
       st_linestring(geom).st_numpoints()         st_numpoints
from a6_lrs;