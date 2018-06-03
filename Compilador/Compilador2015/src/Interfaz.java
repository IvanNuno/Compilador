import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;


public class Interfaz extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	private JTextArea textArea_1;
	private JTextArea rows;
	
	private ImageIcon compilar=new ImageIcon("Iconos/play.png");
	private ImageIcon tetruena=new ImageIcon("Iconos/paste.png");
	//private ImageIcon abrir=new ImageIcon("folder_open/play.png");

	public Interfaz() {
		setTitle("VIAN Compilador");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 717, 605);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{349, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel panel_principal = new JPanel();
		GridBagConstraints gbc_panel_principal = new GridBagConstraints();
		gbc_panel_principal.insets = new Insets(0, 0, 5, 0);
		gbc_panel_principal.fill = GridBagConstraints.BOTH;
		gbc_panel_principal.gridx = 0;
		gbc_panel_principal.gridy = 0;
		contentPane.add(panel_principal, gbc_panel_principal);
		panel_principal.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_principal.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);
		textArea.setFont(new Font("Monospaced", Font.BOLD, 13));
		scrollPane.setViewportView(textArea);
		
		JLabel lblCodigo = new JLabel("Codigo");
		lblCodigo.setForeground(Color.GRAY);
		scrollPane.setColumnHeaderView(lblCodigo);
		
		rows = new JTextArea();
		rows.setForeground(Color.GRAY);
		rows.setEditable(false);
		rows.setFont(new Font("Monospaced", Font.PLAIN, 13));
		rows.setBackground(SystemColor.control);
		scrollPane.setRowHeaderView(rows);
		
		JToolBar toolBar = new JToolBar();
		panel_principal.add(toolBar, BorderLayout.NORTH);
		
		JButton btnBoton = new JButton(compilar);
		btnBoton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Analizadores x = new Analizadores();
				x.entrada=textArea.getText();
				iniciarrows();
				renglones();
				x.scan();
				textArea_1.setText("");
				if( Analizadores.errores.equalsIgnoreCase("") ){
					textArea_1.setText("			COMPILACION CORRECTA ");
					Archivo f=new Archivo();
					f.escribir(Analizadores.codigo_pl0);
				}
				else
					textArea_1.setText(Analizadores.errores);
			}
		});
		toolBar.add(btnBoton);
		
		JButton btnBoton1 = new JButton(tetruena);
		btnBoton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Archivo f=new Archivo();
				f.leer();
				textArea.setText(f.leido);
			}
		});
		toolBar.add(btnBoton1);
		
		JPanel panel_err = new JPanel();
		GridBagConstraints gbc_panel_err = new GridBagConstraints();
		gbc_panel_err.fill = GridBagConstraints.BOTH;
		gbc_panel_err.gridx = 0;
		gbc_panel_err.gridy = 1;
		contentPane.add(panel_err, gbc_panel_err);
		panel_err.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_err.add(scrollPane_1, BorderLayout.CENTER);
		
		textArea_1 = new JTextArea();
		textArea_1.setEditable(false);
		textArea_1.setForeground(Color.RED);
		scrollPane_1.setViewportView(textArea_1);
		
		JLabel Errores = new JLabel("Errores");
		Errores.setForeground(Color.GRAY);
		scrollPane_1.setColumnHeaderView(Errores);
	    iniciarrows();
	}
	
	public void iniciarrows() {
		rows.setText("   ");
	}
	
	public void renglones() {
		rows.setText("");
		int ren=0;
		String cad=textArea.getText();
		for(int i=0; i<cad.length(); i++) {
			if(cad.charAt(i)=='\n') {
				ren++;
				rows.append( ren+"  \n" );
			}
		}
		ren++;
		rows.append( ren+"  \n" );
	}

}
