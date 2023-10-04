
package controlador;

import futbol.Futbol;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultCaret;
import modelo.Equipo;
import modelo.Partido;
import modelo.Torneo;
import vista.*;

/**
 *
 * @author Cegamer
 */
public class Controlador {

    Principal principal = new Principal();
    Login login = new Login();
    private String srcImagen = "src/Datos/Imagenes/default.png";

    public void mostrarVistaAgregarEquipo() {
        var ventana = new AgregarEquipo();
        ventana.show();
        ventana.agregarImagenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                srcImagen = cargarImagenYRedimensionar(ventana);
                System.out.println(srcImagen);
            }
        });

        ventana.botonAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Equipo equipo = new Equipo(ventana.jTextField1.getText(), srcImagen);
                Torneo.agregarEquipo(equipo);
                actualizarTabla();
                ventana.dispose();
                srcImagen = "src/Datos/Imagenes/default.png";

            }
        });
    }

    public String cargarImagenYRedimensionar(AgregarEquipo ventana) {
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(null);
        String rutaDestino = "";
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();

            try {
                BufferedImage imagenOriginal = ImageIO.read(archivoSeleccionado);

                int nuevoAncho = 80;
                int nuevoAlto = 80;

                BufferedImage imagenRedimensionada = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = imagenRedimensionada.createGraphics();
                AffineTransform at = AffineTransform.getScaleInstance((double) nuevoAncho / imagenOriginal.getWidth(), (double) nuevoAlto / imagenOriginal.getHeight());
                g2d.drawRenderedImage(imagenOriginal, at);
                g2d.dispose();

                ImageIcon imagenIcono = new ImageIcon(imagenRedimensionada);
                ventana.imagen.setIcon(imagenIcono);

                rutaDestino = "src/Datos/Imagenes/" + archivoSeleccionado.getName();
                File destinoFile = new File(rutaDestino);
                ImageIO.write(imagenRedimensionada, "PNG", destinoFile);
                return rutaDestino;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return srcImagen;
    }

    void cargarImagenes(AgregarPartido ventana) {
        String rutaImagen = Torneo.getEquipoByName(ventana.selectorLocal.getSelectedItem().toString()).getRutaEscudo();
        String rutaImagenVisitante = Torneo.getEquipoByName(ventana.selectorVisitante.getSelectedItem().toString()).getRutaEscudo();
        System.out.println(rutaImagen);
        try {
            BufferedImage imagen = ImageIO.read(new File(rutaImagen));
            ImageIcon icono = new ImageIcon(imagen);
            ventana.imagenLocal.setIcon(icono);

            BufferedImage imagen2 = ImageIO.read(new File(rutaImagenVisitante));
            ImageIcon icono2 = new ImageIcon(imagen2);
            ventana.imagenVisitante.setIcon(icono2);

        } catch (IOException exception) {
            System.err.println("Error en Imagen" + rutaImagen);
        }

    }

    public void mostrarVistaAgregarPartido() {
        var ventana = new AgregarPartido();

        for (Equipo equipo : Torneo.getEquipos()) {
            ventana.selectorLocal.addItem(equipo.getNombre());
            ventana.selectorVisitante.addItem(equipo.getNombre());
        }

        cargarImagenes(ventana);
        ventana.selectorLocal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarImagenes(ventana);
            }
        });

        ventana.selectorVisitante.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarImagenes(ventana);
            }
        });

        ventana.botonAgregarPartido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ventana.selectorLocal.getSelectedItem().toString().equals(ventana.selectorVisitante.getSelectedItem().toString())) {
                    ventana.errorLabel.setText("No puede seleccionar el mismo Equipo como local y Visitante");
                } else {
                    Equipo Local = Torneo.getEquipoByName(ventana.selectorLocal.getSelectedItem().toString());
                    Equipo Visitante = Torneo.getEquipoByName(ventana.selectorVisitante.getSelectedItem().toString());
                    int golesLocal = Integer.parseInt(ventana.inputGolesLocal.getText());
                    int golesVisitante = Integer.parseInt(ventana.inputGolesVisitante.getText());
                    Torneo.agregarPartido(new Partido(Local, Visitante, golesLocal, golesVisitante));
                    actualizarTabla();
                    ventana.dispose();
                }
            }
        });

        ventana.show();
    }
    
    public void mostrarLogin(){
        login.jButton1.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                mostrarVistaPrincipal();
            }
        });
        
        login.show();
    }

    public void mostrarVistaPrincipal() {
        Torneo.cargarEquipos();
        Torneo.cargarPartidos();
        actualizarTabla();
        principal.botonNuevoEquipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarVistaAgregarEquipo();
            }
        });
        principal.botonNuevoPartido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarVistaAgregarPartido();
            }
        });
        principal.show();
    }

    void actualizarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Nombre");
        modelo.addColumn("Ganados");
        modelo.addColumn("Perdidos");
        modelo.addColumn("Empatados");
        modelo.addColumn("Puntos");
        for (Equipo equipo : Torneo.getEquipos()) {
            Object[] fila = {
                equipo.getNombre(),
                equipo.getGanados(),
                equipo.getPerdidos(),
                equipo.getEmpatados(),
                equipo.getPuntos()
            };
            modelo.addRow(fila);
        }

        Torneo.guardarEquipos();
        Torneo.guardarPartidos();
        principal.jTable1.setModel(modelo);
       
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        principal.jTable1.setRowSorter(sorter);
        Comparator<Integer> puntosComparator = (p1, p2) -> p2 - p1; // Orden descendente

        sorter.setComparator(4, puntosComparator); // La columna de "Puntos" es la quinta (0-indexed)
        JPanel frame = new JPanel();
        JPanel panels = new JPanel();
        JScrollPane scrPane = new JScrollPane(panels);
        scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrPane);
        for (Partido partido : Torneo.getPartidos()) {
            try {

                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout()); // Usa un layout apropiado
                JLabel texto = new JLabel(partido.getLocal().getNombre() + " " + partido.getGolesLocal() + " VS " + partido.getGolesVisitante() + " " + partido.getVisitante().getNombre());

                panel.setSize(400, 100);
                panel.setBackground(Color.red);

                JLabel img1 = new JLabel("");
                BufferedImage imagen = ImageIO.read(new File(partido.getLocal().getRutaEscudo()));
                ImageIcon icono = new ImageIcon(imagen);
                img1.setIcon(icono);

                JLabel img2 = new JLabel("");
                BufferedImage imagen2 = ImageIO.read(new File(partido.getVisitante().getRutaEscudo()));
                ImageIcon icono2 = new ImageIcon(imagen2);
                img2.setIcon(icono2);
                
                
                panel.add(img1);
                panel.add(texto);
                panel.add(img2);
                principal.jPanel4.add(panel);
                principal.jPanel4.add(new Label(""));
                principal.jPanel4.setLayout(new BoxLayout(principal.jPanel4, BoxLayout.Y_AXIS));

            } catch (Exception e) {
            }
        }
    }
}
