-- cw 1
-- zad a
INSERT INTO USER_SDO_GEOM_METADATA
VALUES (
'FIGURY',
'KSZTALT',
MDSYS.SDO_DIM_ARRAY(
 MDSYS.SDO_DIM_ELEMENT('X', 0, 10, 0.01),
 MDSYS.SDO_DIM_ELEMENT('Y', 0, 10, 0.01) ),
 null
);

-- zad b
select sdo_tune.estimate_rtree_index_size(3000000, 8192, 10, 2, 0) from dual;

-- zad c
create index FIGURY_KSZTALT_IDX
on FIGURY(KSZTALT)
INDEXTYPE IS MDSYS.SPATIAL_INDEX_V2;

-- zad d
select id
from figury
where sdo_filter(
    ksztalt,
    sdo_geometry(2001, null, sdo_point_type(3, 3, null), null, null)
) = 'TRUE';
-- nie jest to prawda, wykorzystywane jedynie przybli?enie na podstawie indeksu

-- zad e
select id
from figury
where sdo_relate(
    ksztalt,
    sdo_geometry(2001, null, sdo_point_type(3, 3, null), null, null),
    'mask=ANYINTERACT'
) = 'TRUE';
-- to jest prawda, z przybli?e? za pomoc? metod dok?adnych zosta? wyznaczony punkt

-- cw 2
-- zad a
with warsaw_geom as(
    select geom
    from major_cities
    where city_name = 'Warsaw'
)
select mc.city_name as miasto, sdo_nn_distance(1) as odl
from major_cities mc, warsaw_geom wg
where sdo_nn(
    mc.geom,
    wg.geom,
    'sdo_num_res=9 unit=km',
    1
) = 'TRUE' and mc.city_name != 'Warsaw';

-- zad b
with warsaw_geom as(
    select geom
    from major_cities
    where city_name = 'Warsaw'
)
select mc.city_name as miasto
from major_cities mc, warsaw_geom wg
where sdo_within_distance(
    mc.geom,
    wg.geom,
    'distance=100 unit=km'
) = 'TRUE' and mc.city_name != 'Warsaw';

-- zad c
with slovakia_geom as(
    select cntry_name, geom
    from country_boundaries
    where cntry_name = 'Slovakia'
)
select sg.cntry_name as kraj, mc.city_name as miasto
from major_cities mc, slovakia_geom sg
where sdo_relate(
    sg.geom,
    mc.geom,
    'mask=CONTAINS'
) = 'TRUE';

-- zad d
with poland_geom as(
    select cntry_name, geom
    from country_boundaries
    where cntry_name = 'Poland'
)
select cb.cntry_name as panstwo, sdo_geom.sdo_distance(cb.geom, pg.geom, 1, 'unit=km') as odl
from country_boundaries cb, poland_geom pg
where sdo_relate(
    pg.geom,
    cb.geom,
    'mask=TOUCH'
) != 'TRUE' and cb.cntry_name != 'Poland';

-- cw 3
-- zad a
with poland_geom as(
    select cntry_name, geom
    from country_boundaries
    where cntry_name = 'Poland'
)
select  cb.cntry_name,
        SDO_GEOM.SDO_LENGTH(SDO_GEOM.SDO_INTERSECTION(cb.GEOM, pg.GEOM, 1), 1, 'unit=km') as odleglosc
from COUNTRY_BOUNDARIES cb, poland_geom pg
where sdo_relate(
    pg.geom,
    cb.geom,
    'mask=TOUCH'
) = 'TRUE';

-- zad b
select cb.cntry_name
from COUNTRY_BOUNDARIES cb
where sdo_geom.sdo_area(cb.geom) = (
    select max(sdo_geom.sdo_area(geom)) from COUNTRY_BOUNDARIES
);

-- zad c
with warsaw_geom as(
    select geom
    from major_cities
    where city_name = 'Warsaw'
),
lodz_geom as (
    select geom
    from major_cities
    where city_name = 'Lodz'
)
select  sdo_geom.sdo_area(
            sdo_geom.sdo_mbr(
                sdo_geom.sdo_union(
                    wg.geom,
                    lg.geom,
                    0.01
                )
            ), 0.01, 'unit=SQ_KM'
        ) as SQ_KM
from warsaw_geom wg, lodz_geom lg;

-- zad d

