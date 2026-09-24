// Implementación del servicio que genera la factura PDF a partir del carrito.
package com.tienda.virtual.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tienda.virtual.model.Producto;
import com.tienda.virtual.model.Usuario;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.math.BigDecimal;
import java.util.List;


@Service
public class CarritoServiceImpl implements CarritoService {

    @Override
    public byte[] generarFactura(List<Producto> carrito, Usuario usuario, Long pedidoId, String pedidoEstado, java.time.LocalDateTime pedidoFecha) {
        if (carrito == null || carrito.isEmpty()) return null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Datos de la empresa.
            doc.add(new Paragraph("OriginalFragance", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 25)));
            doc.add(new Paragraph("Perfumes Originales, Que cada fragancia no solo acompañe tu día, sino que deje un recuerdo imposible de olvidar. ^^Paco Rabanne^^"));
            doc.add(new Paragraph("Tunja Centro"));
            doc.add(new Paragraph("Correo Corporativo originalfragance516@gmail.com"));

            // Título de la factura.
            doc.add(new Paragraph("Comprobante de pedido - Recibo de compra", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));
            doc.add(new Paragraph(" ")); // Espacio

            // Datos básicos del comprador.
            doc.add(new Paragraph("Datos del Comprador:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.add(new Paragraph("Usuario: " + usuario.getNombreUsuario()));
            doc.add(new Paragraph("Nombres: " + usuario.getNombres()));
            doc.add(new Paragraph("Apellidos: " + usuario.getApellidos()));
            doc.add(new Paragraph("Celular: " + usuario.getCelular()));
            doc.add(new Paragraph("Correo: " + usuario.getCorreo()));
            doc.add(new Paragraph("Dirección de entrega: " + usuario.getDireccionEnvio()));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Datos del Pedido:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.add(new Paragraph("Número de pedido: " + pedidoId));
            doc.add(new Paragraph("Estado: " + pedidoEstado));
            doc.add(new Paragraph("Fecha: " + pedidoFecha));
            doc.add(new Paragraph(" "));

            // Título de productos comprados
            doc.add(new Paragraph("Productos Comprados:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.add(new Paragraph(" ")); // Espacio

            // Tabla de productos.
            PdfPTable table = new PdfPTable(3);
            table.addCell(new PdfPCell(new Paragraph("Producto")));
            table.addCell(new PdfPCell(new Paragraph("Precio")));
            table.addCell(new PdfPCell(new Paragraph("Descripción")));


            // se importa NumberFormatLocale y se crea una instancia para ser llamada en donde se vaya mostrar el precio a miles
            NumberFormat formato = NumberFormat.getInstance(new Locale("es", "ES"));
            // variable auxiliar para iniciar en cero el total para operacion de suma
            BigDecimal total = BigDecimal.ZERO;
            for (Producto p : carrito) {
                table.addCell(p.getNombre());
                table.addCell("$" + formato.format(p.getPrecio()));   // precio formateado a miles
                table.addCell(crearDescripcionFactura(p.getDescripcion()));
                total = total.add(p.getPrecio()); // hace la operacion de suma(.add) inicializada en cero.ZERO
            }

            // Precio final formateado a miles e instrucciones de envío del comprador.
            doc.add(table);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Precio Total Facturado: $" + formato.format(total), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15)));
            doc.add(new Paragraph(" ")); // Espacio
            doc.add(new Paragraph("la factura se enviará a correo y celular registrado, emitiremos tu factura electrónica validada por la DIAN, Los productos llegarán a la dirección registrada por el usuario. La transportadora se comunicara contigo para la entrega.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            doc.add(new Paragraph(" ")); // Espacio
            doc.add(new Paragraph("Cualquier duda o inquietud comunicarse con nuestras líneas de atención. líneas telefónicas WhatsApp 3112463665 o al correo: originalfragance516@gmail.com", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            doc.add(new Paragraph(" ")); // Espacio
            doc.add(new Paragraph("Atrévete a ser diferente. Atrévete a dejar tu esencia. ^^paco rabanne^^", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)));

            doc.close(); // cierra el documento pdf y lo guarda en la variable out
            return out.toByteArray(); // retorna el archivo pdf en bytes
        } catch(Exception e) { // excepcion en caso de error en la generacion del pdf
            e.printStackTrace(); // imprime el error en consola
            return null; // retorna null en caso de error
        }
    }

    // Convierte la descripción almacenada con HTML en una descripción adecuada para el PDF.
    // El precio anterior se muestra debajo, en menor tamaño, gris y tachado.
    private Paragraph crearDescripcionFactura(String descripcion) {
        Paragraph resultado = new Paragraph();

        if (descripcion == null || descripcion.isEmpty()) {
            return resultado;
        }

        String texto = descripcion;
        String precioAnterior = null;

        int inicioPrecio = texto.indexOf("<s");
        if (inicioPrecio >= 0) {
            int inicioContenido = texto.indexOf(">", inicioPrecio);
            int finPrecio = texto.indexOf("</s>", inicioContenido);

            if (inicioContenido >= 0 && finPrecio >= 0) {
                precioAnterior = texto.substring(inicioContenido + 1, finPrecio);
                texto = texto.substring(0, inicioPrecio).trim();
            }
        }

        // Elimina cualquier etiqueta HTML que pueda quedar en la descripción.
        texto = texto.replaceAll("<[^>]*>", "").trim();
        resultado.add(new Chunk(texto));

        if (precioAnterior != null && !precioAnterior.isEmpty()) {
            resultado.add(Chunk.NEWLINE);
            Font fuentePrecioAnterior = FontFactory.getFont(
                    FontFactory.HELVETICA,
                    8,
                    Font.STRIKETHRU,
                    BaseColor.GRAY
            );
            resultado.add(new Chunk(precioAnterior, fuentePrecioAnterior));
        }

        return resultado;
    }
}
