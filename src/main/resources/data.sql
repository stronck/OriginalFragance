-- Datos iniciales de productos para entornos donde PostgreSQL no ejecuta sql/ecommerce.sql automáticamente.
-- Los INSERT son idempotentes para evitar duplicados en cada arranque.

INSERT INTO producto (nombre, descripcion, precio)
SELECT 'PACO RABANNE c- 1 FAME 80ml',
       'Un perfume femenino con notas de menta, sangre de dragón y ámbar, ideal para ocasiones nocturnas. Duración de 12 a 24 horas. Envase dorado con detalle de medallón. <div><s style="font-size: 0.8em; color: #777;">$649.999</s></div>',
       549900.00
WHERE NOT EXISTS (
    SELECT 1 FROM producto WHERE nombre = 'PACO RABANNE c- 1 FAME 80ml'
);

INSERT INTO producto (nombre, descripcion, precio)
SELECT 'PACO RABANNE c- 1 PURE XS BLACK 100ml',
       'Un perfume masculino que combina notas de café y vainilla intensa, ofreciendo un aroma sensual con un toque de misterio y seducción. <div><s style="font-size: 0.8em; color: #777;">$639.999</s></div>',
       579900.00
WHERE NOT EXISTS (
    SELECT 1 FROM producto WHERE nombre = 'PACO RABANNE c- 1 PURE XS BLACK 100ml'
);

INSERT INTO producto (nombre, descripcion, precio)
SELECT 'PACO RABANNE c- 1 LADY MILLION 80ml',
       'Un perfume femenino, fresco, floral y seductor que combina neroli, jazmín, patchouli y especias, brindando un toque de glamour y sofisticación. <div><s style="font-size: 0.8em; color: #777;">$529.999</s></div>',
       469900.00
WHERE NOT EXISTS (
    SELECT 1 FROM producto WHERE nombre = 'PACO RABANNE c- 1 LADY MILLION 80ml'
);

INSERT INTO producto (nombre, descripcion, precio)
SELECT 'PACO RABANNE c- 1 PURE XS 100ml',
       'Un perfume masculino acuático y fresco que mezcla notas de pomelo, mandarina y flor de naranjo, aportando dinamismo y aventura. <div><s style="font-size: 0.8em; color: #777;">$649.999</s></div>',
       569900.00
WHERE NOT EXISTS (
    SELECT 1 FROM producto WHERE nombre = 'PACO RABANNE c- 1 PURE XS 100ml'
);
