import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {
	private static final String opCrearCliente = "1";
	private static final String opCrearFactura = "2";
	private static final String opMostrarCliente = "3";
	private static final String opMostrarFactura = "4";

	private static final String opSalir = "0";
	
	private static final String opError = "err";
	
	private static Scanner consola = new Scanner(System.in);
	
	private static EntityManagerFactory emf;
	private static EntityManager em;
	
	public static void main(String[] args) {
		boolean salir = false;
		
		// Abre la BD.
		emf = Persistence.createEntityManagerFactory("tapas");
		System.out.println();
		System.out.println("Info: EntityManagerFactory creado.");
		
		em = emf.createEntityManager();
		System.out.println();
		System.out.println("Info: EntityManager creado.");
		
		while (salir == false) {
			limpiarPantalla();
			switch(opcionMenu()) {
			case opSalir: salir = true; break;
			case opCrearCliente: menuCrearCliente(); break;
			case opCrearFactura: menuCrearFactura(); break;
			case opMostrarCliente: menuMostrarCliente(); break;
			case opMostrarFactura: menuMostrarFactura(); break;
			case opError:
				System.out.println();
				System.out.println("Opción incorrecta. Vuelva a intentarlo.");
				enterParaContinuar();
			break;
			}
		}
		
		// Cierra la BD.
		if (em != null) em.close();
		System.out.println();
		System.out.println("Info: EntityManager finalizado.");
		if (emf != null) emf.close();
		System.out.println();
		System.out.println("Info: EntityManagerFactory finalizado.");
		
		// Finalizo el programa.
		consola.close();
		System.out.println();
		System.out.println("Proceso finalizado.");
		System.exit(0);
	}
	
	static public void enterParaContinuar() {
		System.out.println("");
		System.out.println("Presione Enter para continuar.");
		consola.nextLine();
	}
	
	// Funcionamiento: muestra el menú principal y obtiene
	// la opción del usuario. Devuelve "err" si se eligió una
	// opción incorrecta.
	static public String opcionMenu() {
		String input = opError; // Por defecto es opción inválida.
		
		System.out.println("Menú principal");
		System.out.println("--------------");
		System.out.println(opCrearCliente + ") Crear cliente.");
		System.out.println(opCrearFactura + ") Crear Factura.");
		System.out.println(opMostrarCliente + ") Mostrar cliente.");
		System.out.println(opMostrarFactura + ") Mostrar factura.");
		System.out.println();
		System.out.println(opSalir + ") Salir.");
		System.out.println();
		System.out.print("Ingrese una opción: ");
		
		input = consola.nextLine();
		switch(input) {
		case opSalir:
		case opCrearCliente:
		case opCrearFactura:
		case opMostrarCliente:
		case opMostrarFactura:
		break;
		default: input = "err";
		}
		
		return input;
	}
	
	// Funcionamiento: muestra el menú de creación de cliente
	// y le indica al usuario que ingrese los datos para su creación.
	// Sólo permite crear clientes que no existan en la BD y cuya
	// descripción no esté vacía. También brinda la opción de cancelar
	// la creación.
	@SuppressWarnings("unchecked")
	static public void menuCrearCliente() {
		String input;
		int codigoCliente;
		boolean volver = false;
		boolean repetir, noExiste;
		Query query;
		List<Cliente> clientes;
		
		while (!volver) {
			limpiarPantalla();
			System.out.println("Menú de creación de cliente");
			System.out.println("---------------------------");
			
			repetir = true;
			while(repetir) {
				
				// El usuario ingresa el ID o sale.
				System.out.println();
				System.out.print("Ingrese el ID ó 0 para cancelar: ");
				input = consola.nextLine();
				if (input.equals("0")) {
					repetir = false;
					volver = true;
					
				} else { 
					try {
						
						// Si el ID no es válido, error.
						codigoCliente = Integer.parseInt(input);
						if (codigoCliente < 0) {
							System.out.println();
							System.out.println("El ID debe ser mayor a cero.");
							enterParaContinuar();
						}

						else {
							
							// Si el ID ya existe, error.
							em.getTransaction().begin();
							em.flush();
							query = em.createNamedQuery("cliente.porID");
							query.setParameter("id", codigoCliente);
							clientes = query.getResultList();
							em.getTransaction().commit();
							if(!clientes.isEmpty()) {
								System.out.println();
								System.out.println("Ya existe el ID de cliente.");
								enterParaContinuar();

							} else {
								repetir = true;
								while(repetir) {
								
									// El usuario ingresa la descripción o sale.
									System.out.println();
									System.out.print("Ingrese la descripción ó 0 para cancelar: ");
									input = consola.nextLine();
									if (input.equals("0")) {
										repetir = false;
										volver = true;
									
									// Si la descripción está vacía, error.
									} else if (input.isEmpty()) {
										System.out.println();
										System.out.println("La descripción no puede estar vacía.");
										enterParaContinuar();
										
									} else {
										
										// Persiste el cliente.
										Cliente clienteActual = new Cliente();
										clienteActual.setID(codigoCliente);
										clienteActual.setDescr(input);
										try {
											em.getTransaction().begin();
											em.persist(clienteActual);
											em.getTransaction().commit();
											
											while (repetir) {
												System.out.println();
												System.out.print("¿Desea ingresar otro cliente? [S/N]: ");
												input = consola.nextLine();
												switch(input) {
												case "n", "N":
													repetir = false;
													volver = true;
												break;
												case "s", "S":
													repetir = false;
												break;
												default:
													System.out.println();
													System.out.println("Ingresó una opción incorrecta. Vuelva a intentarlo.");
													enterParaContinuar();
												}
											}
										} catch(Exception e) {
											System.out.println();
											System.out.println("Info: error.");
											e.printStackTrace();
											repetir = false;
											volver = true;
											enterParaContinuar();
										}
									}
								}
							}
						}
					} catch (NumberFormatException e) {
						System.out.println();
						System.out.println("El ID debe ser numérico.");
						enterParaContinuar();
					}
				}
			}
		}
	}

	// Funcionamiento: muestra el menú de creación de cliente
	// y le indica al usuario que ingrese los datos para su creación.
	// Sólo permite crear clientes que no existan en la BD y cuya
	// descripción no esté vacía. También brinda la opción de cancelar
	// la creación.
	@SuppressWarnings("unchecked")
	static public void menuCrearFactura() {
		String input;
		int numeroFactura, codigoCliente;
		float importeFactura;
		boolean volver = false;
		boolean repetir;
		Query query;
		List<Cliente> clientes;
		List<Factura> facturas;
		
		while (!volver) {
			limpiarPantalla();
			System.out.println("Menú de creación de factura");
			System.out.println("---------------------------");
			
			repetir = true;
			while(repetir) {
				
				// El usuario ingresa el número o sale.
				System.out.println();
				System.out.print("Ingrese el número ó 0 para cancelar: ");
				input = consola.nextLine();
				if (input.equals("0")) {
					repetir = false;
					volver = true;
					
				} else { 
					try {
						
						// Si el número ingresado no es válido, error.
						numeroFactura = Integer.parseInt(input);
						if (numeroFactura < 0) {
							System.out.println();
							System.out.println("El número debe ser mayor a cero.");
							enterParaContinuar();
							
						} else {
							
							// Si el número ya existe, error.
							query = em.createNamedQuery("factura.porNro");
							query.setParameter("nro", numeroFactura);
							facturas = query.getResultList();
							if(!facturas.isEmpty()) {
								System.out.println();
								System.out.println("Ya existe el número de factura.");
								enterParaContinuar();
								
							} else {
								
								repetir = true;
								while(repetir) {
									
									// El usuario ingresa el ID de cliente o sale.
									System.out.println();
									System.out.print("Ingrese el ID de cliente ó 0 para cancelar: ");
									input = consola.nextLine();
									if (input.equals("0")) {
										repetir = false;
										volver = true;
										
									} else {
										try {
											
											// Si el ID ingresado no es válido, error.
											codigoCliente = Integer.parseInt(input);
											if (codigoCliente < 0) {
												System.out.println();
												System.out.println("El código de cliente debe ser mayor a cero.");
												enterParaContinuar();
												
											} else {
													
												// Si el ID de cliente no existe, error.
												query = em.createNamedQuery("cliente.porID");
												query.setParameter("id", codigoCliente);
												clientes = query.getResultList();
												if(clientes.isEmpty()) {
													System.out.println();
													System.out.println("No existe el ID de cliente.");
													enterParaContinuar();
													
												} else {
													
													repetir = true;
													while (repetir) {
														
														// El usuario ingresa el importe ó sale.
														System.out.println();
														System.out.print("Ingrese el importe de la factura ó 0 para cancelar: ");
														input = consola.nextLine();
														if (input.equals("0")) {
															repetir = false;
															volver = true;
														
														} else {
															try {
																importeFactura = Float.parseFloat(input);
																if (importeFactura < 0) {
																	System.out.println();
																	System.out.println("El importe debe ser mayor a cero.");
																	enterParaContinuar();
																	
																} else {
																
																	
																	try {
																		// Guarda la factura.
																		Factura facturaGuardar = new Factura();
																		facturaGuardar.setNro(numeroFactura);
																		facturaGuardar.setIdCliente(codigoCliente);
																		facturaGuardar.setImporte(importeFactura);
																		em.getTransaction().begin();
																		em.persist(facturaGuardar);
																		em.getTransaction().commit();
																	
																		while (repetir) {
																			System.out.println();
																			System.out.print("¿Desea ingresar otra factura? [S/N]: ");
																			input = consola.nextLine();
																			switch(input) {
																			case "n", "N":
																				repetir = false;
																				volver = true;
																			break;
																			case "s", "S":
																				repetir = false;
																			break;
																			default:
																				System.out.println();
																				System.out.println("Ingresó una opción incorrecta. Vuelva a intentarlo.");
																				enterParaContinuar();
																			}
																		} // while
																	} catch(Exception e) {
																		System.out.println();
																		System.out.println("Info: error.");
																		e.printStackTrace();
																		repetir = false;
																		volver = true;
																		enterParaContinuar();
																	}
																}
															} catch (NumberFormatException e) {
																System.out.println();
																System.out.println("El importe debe ser numérico.");
																enterParaContinuar();
															}
														}
													} // while
												}
											}
										} catch (NumberFormatException e) {
											System.out.println();
											System.out.println("El ID debe ser numérico.");
											enterParaContinuar();
										}
									}
								} // while
							}
						}
					} catch (NumberFormatException e) {
						System.out.println();
						System.out.println("El ID debe ser numérico.");
						enterParaContinuar();
					}
				}
			}
		}
	}

	
	// Funcionamiento: le pregunta al usuario si quiere enlistar
	// todos los clientes o si quiere mostrar un cliente en base
	// a su ID.
	@SuppressWarnings("unchecked")
	static public void menuMostrarCliente() {
		String input;
		int codigoCliente;
		boolean repetir = true;
		Query query;
		List<Cliente> clientes;
		
		limpiarPantalla();
		while(repetir) {
			System.out.println();
			System.out.print("Ingrese el ID del cliente a mostrar o presione Enter para mostrar todos: ");
			input = consola.nextLine();
			
			if(input.isEmpty()) {
				
				// Si no hay clientes, avisa.
				query = em.createNamedQuery("cliente.todos");
				clientes = query.getResultList();
				if(clientes.isEmpty()) {
					System.out.println();
					System.out.println("No hay clientes.");
					
				// Si no, muestra todos los clientes.
				} else {
					for(int i = 0; i < clientes.size(); i++) {
						System.out.println();
						System.out.println(clientes.get(i).toString());
					}
				}
				
				repetir = false;
				enterParaContinuar();
			} else {
				try {
					
					// Si el ID de cliente es incorrecto, error.
					codigoCliente = Integer.parseInt(input);
					if(codigoCliente <= 0) {
						System.out.println();
						System.out.println("El ID debe ser mayor a cero. Vuelva a intentarlo.");
					} else {
						
						// Si no hay cliente con el ID, avisa.
						query = em.createNamedQuery("cliente.porID");
						query.setParameter("id", codigoCliente);
						clientes = query.getResultList();
						if(clientes.isEmpty()) {
							System.out.println();
							System.out.println("No hay cliente con el ID " + codigoCliente + ".");
							
						// Muestra el cliente.
						} else {
							System.out.println();
							System.out.println(clientes.get(0).toString());
						}
						
						repetir = false;
					}
					enterParaContinuar();
				} catch (NumberFormatException e) {
					System.out.println();
					System.out.println("El código debe ser numérico. Vuelva a intentarlo.");
					enterParaContinuar();
				}
			}
		}
	}

	// Funcionamiento: le pregunta al usuario si quiere enlistar
	// todas las facturas o si quiere mostrar una factura en base
	// a su número.
	@SuppressWarnings("unchecked")
	static public void menuMostrarFactura() {
		String input;
		int numeroFactura;
		boolean repetir = true;
		Query query;
		List<Factura> facturas;
		
		limpiarPantalla();
		while(repetir) {
			System.out.println();
			System.out.print("Ingrese el número de la factura a mostrar o presione Enter para mostrar todas: ");
			input = consola.nextLine();
			
			if(input.isEmpty()) {
				
				// Si no hay facturas, avisa.
				query = em.createNamedQuery("factura.todas");
				facturas = query.getResultList();
				if(facturas.isEmpty()) {
					System.out.println();
					System.out.println("No hay facturas.");
					
				// Muestra todas las facturas.
				} else {
					for(int i = 0; i < facturas.size(); i++) {
						System.out.println();
						System.out.println(facturas.get(i).toString());
					}
				}
				
				repetir = false;
				enterParaContinuar();
			} else {
				try {
					
					// Si el número es incorrecto, error.
					numeroFactura = Integer.parseInt(input);
					if(numeroFactura <= 0) {
						System.out.println();
						System.out.println("El número debe ser mayor a cero. Vuelva a intentarlo.");
						
					} else {
						
						// Si no hay factura con el número, avisa.
						query = em.createNamedQuery("factura.porNro");
						query.setParameter("nro", numeroFactura);
						facturas = query.getResultList();
						if(facturas.isEmpty()) {
							System.out.println();
							System.out.println("No hay factura con el número " + numeroFactura + ".");
							
						// Muestra la factura.
						} else {
							System.out.println();
							System.out.println(facturas.get(0).toString());
						}
						
						repetir = false;
					}
					enterParaContinuar();
				} catch (NumberFormatException e) {
					System.out.println();
					System.out.println("El valor debe ser numérico. Vuelva a intentarlo.");
					enterParaContinuar();
				}
			}
		}
	}
	
	// Funcionamiento: limpia la pantalla de la consola.
	static public void limpiarPantalla() {
		for(int i = 0; i < 100; i++)
			System.out.println();
	}
}