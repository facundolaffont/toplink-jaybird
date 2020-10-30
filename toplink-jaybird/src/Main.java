import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {
	private static final String opCrearCliente = "1";
	private static final String opCrearFactura = "2";
	private static final String opMostrarCliente = "3";
	private static final String opMostrarFactura = "4";
	private static final String opBajaCliente = "5";
	private static final String opBajaFactura = "6";
	private static final String opModifCliente = "7";
	private static final String opModifFactura = "8";

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
			case opBajaCliente: menuBajaCliente(); break;
			case opBajaFactura: menuBajaFactura(); break;
			case opModifCliente: menuModifCliente(); break;
			case opModifFactura: menuModifFactura(); break;
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
		System.out.println(opBajaCliente + ") Baja Cliente.");
		System.out.println(opBajaFactura + ") Baja Factura.");
		System.out.println(opModifCliente + ") Modificar cliente.");
		System.out.println(opModifFactura + ") Modificar factura.");
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
		case opBajaCliente:
		case opBajaFactura:
		case opModifCliente:
		case opModifFactura:
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
		boolean repetir;
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
							query = em.createNamedQuery("cliente.porID");
							query.setParameter("id", codigoCliente);
							clientes = query.getResultList();
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
											System.out.println();
											System.out.println("Cliente creado.");
											
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
																		System.out.println();
																		System.out.println("Factura creada.");
																	
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
		
		while(repetir) {
			limpiarPantalla();
			System.out.println("Mostrar clientes");
			System.out.println("----------------");
			
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
		
		
		while(repetir) {
			limpiarPantalla();
			System.out.println("Mostrar facturas");
			System.out.println("----------------");
			
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

	@SuppressWarnings("unchecked")
	static public void menuBajaCliente() {
		String input;
		int codigoCliente;
		boolean volver = false;
		boolean repetir; 
		Query query;
		List<Cliente> clientes;
		List<Factura> facturas;
		
		while (!volver) {
			limpiarPantalla();
			System.out.println("Eliminación de clientes");
			System.out.println("-----------------------");
			
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
							
							// Si el ID no existe, error.
							query = em.createNamedQuery("cliente.porID");
							query.setParameter("id", codigoCliente);
							clientes = query.getResultList();
							if(clientes.isEmpty()) {
								System.out.println();
								System.out.println("No existe el ID de cliente.");
								enterParaContinuar();

							} else {
								
								// Si existen facturas con ese ID de cliente, primero se
								// deben borrar las facturas.
								query = em.createNamedQuery("factura.porIdCliente");
								query.setParameter("id", codigoCliente);
								facturas = query.getResultList();
								if(!facturas.isEmpty()) {
									System.out.println();
									System.out.println("Primero debe eliminar las facturas asociadas con el cliente:");
									for(int i = 0; i < facturas.size(); i++) {
										System.out.println();
										System.out.println(facturas.get(i).toString());
									}
									enterParaContinuar();
								} else {
								
									repetir = true;
									while(repetir) {
	
										// Eliminamos al cliente.
										// Como la Lista no dio vacia existe el cliente
										try {
											em.getTransaction().begin();
											em.remove(clientes.get(0));
											em.getTransaction().commit();
											System.out.println();
											System.out.println("Cliente eliminado.");
											
											while (repetir) {
												System.out.println();
												System.out.print("¿Desea dar de Baja otro cliente? [S/N]: ");
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
	
	@SuppressWarnings("unchecked")
	static public void menuBajaFactura() {
		String input;
		int codigoFactura;
		boolean volver = false;
		boolean repetir; 
		Query query;
		List<Factura> facturas;
		
		while (!volver) {
			limpiarPantalla();
			System.out.println("Eliminacion de factura");
			System.out.println("----------------------");
			
			repetir = true;
			while(repetir) {
				
				// El usuario ingresa el número de factura o sale.
				System.out.println();
				System.out.print("Ingrese el número de factura ó 0 para cancelar: ");
				input = consola.nextLine();
				if (input.equals("0")) {
					repetir = false;
					volver = true;
					
				} else { 
					try {
						
						// Si el número de factura no es válido, error.
						codigoFactura = Integer.parseInt(input);
						if (codigoFactura < 0) {
							System.out.println();
							System.out.println("El número de factura debe ser mayor a cero.");
							enterParaContinuar();
						}

						else {
							
							// Si el número de factura no existe, error.
							query = em.createNamedQuery("factura.porNro");
							query.setParameter("nro", codigoFactura);
							facturas = query.getResultList();
							
							if(facturas.isEmpty()) {
								System.out.println();
								System.out.println("No existe el número de factura.");
								enterParaContinuar();

							} else {
								repetir = true;
								while(repetir) {
									// Eliminamos a la factura.
									// Como la lista no está vacía, existe la factura.
									try {
										em.getTransaction().begin();
										em.remove(facturas.get(0));
										em.getTransaction().commit();
										System.out.println();
										System.out.println("Factura eliminada.");
										
										while (repetir) {
											System.out.println();
											System.out.print("¿Desea dar de baja otra factura? [S/N]: ");
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
					} catch (NumberFormatException e) {
						System.out.println();
						System.out.println("El valor debe ser numérico.");
						enterParaContinuar();
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	static public void menuModifCliente() {
		String input;
		boolean repetir = true;
		int codigoCliente;
		Query query;
		List<Cliente> clientes;
		String nuevaDescripcion;
		Cliente clienteModif;
		
		while (repetir) {
			limpiarPantalla();
			System.out.println("Modificación de clientes");
			System.out.println("------------------------");
			
			System.out.print("Ingrese el ID de cliente a modificar, o 0 para volver: ");
			input = consola.nextLine();
			
			if(!esEntero(input))
				notificar("El valor ingresado no es un entero.");
			else {
				codigoCliente = Integer.parseInt(input);
				if(codigoCliente < 0)
					notificar("El valor ingresado no puede ser menor a cero.");
				else if(codigoCliente == 0)
					repetir = false;
				else {
					query = em.createNamedQuery("cliente.porID");
					query.setParameter("id", codigoCliente);
					clientes = query.getResultList();
					if(clientes.isEmpty())
						notificar("No existe el cliente con ID " + codigoCliente);
					else {
						nuevaDescripcion = "";

						System.out.println();
						System.out.println("Descripción actual: " + clientes.get(0).getDescr());

						System.out.println();
						System.out.print("Tipee el nuevo valor, o presione Enter para no modificar: ");
						input = consola.nextLine();

						if(!input.isEmpty()) {
							nuevaDescripcion = input;
						}
						if (nuevaDescripcion.isEmpty())
							notificar("No hubo modificaciones.");
						else {
							clienteModif = new Cliente();
							clienteModif.setID(clientes.get(0).getID());
							clienteModif.setDescr(nuevaDescripcion);
							
							System.out.println();
							System.out.println("-- Datos originales --");
							System.out.println(clientes.get(0).toString());

							System.out.println();
							System.out.println("-- Datos modificados --");
							System.out.println(clienteModif.toString());

							while (repetir) {
								System.out.println();
								System.out.print("¿Desea confirmar los cambios? [S/N]: ");
								input = consola.nextLine();
								switch(input) {
								case "s", "S":
								case "n", "N":
									repetir = false;
								break;
								default:
									notificar("Ingresó una opción incorrecta. Vuelva a intentarlo.");
								}
							}
							
							switch(input) {
							case "s", "S":
								em.getTransaction().begin();
								clientes.get(0).setDescr(nuevaDescripcion);
								em.getTransaction().commit();
								notificar("Se realizaron las modificaciones.");
							break;
							case "n", "N":
								notificar("Se canceló la modificación.");
							break;
							}
							
							repetir = true;
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	static public void menuModifFactura() {
		String input;
		boolean repetir = true;
		int numeroFactura;
		Query query;
		List<Factura> facturas;
		List<Cliente> clientes;
		int nuevoID;
		float nuevoImporte;
		Factura facturaModif;
		
		while (repetir) {
			limpiarPantalla();
			System.out.println("Modificación de factura");
			System.out.println("-----------------------");
			
			System.out.print("Ingrese el número de factura a modificar, o 0 para volver: ");
			input = consola.nextLine();
			
			if(!esEntero(input))
				notificar("El valor ingresado no es un entero.");
			else {
				numeroFactura = Integer.parseInt(input);
				if(numeroFactura < 0)
					notificar("El valor ingresado no puede ser menor a cero.");
				else if(numeroFactura == 0)
					repetir = false;
				else {
					query = em.createNamedQuery("factura.porNro");
					query.setParameter("nro", numeroFactura);
					facturas = query.getResultList();
					if(facturas.isEmpty())
						notificar("No existe la factura con número " + numeroFactura);
					else {

						nuevoID = -1;
						while(repetir) {
							System.out.println();
							System.out.println("ID de cliente actual: " + facturas.get(0).getIdCliente());
	
							System.out.println();
							System.out.print("Tipee el nuevo valor, o presione Enter para no modificar: ");
							input = consola.nextLine();
	
							if(input.isEmpty()) {
								nuevoID = -1;
								repetir = false;
							} else
								if(!esEntero(input))
									notificar("El valor ingresado no es un entero.");
								else {
									nuevoID = Integer.parseInt(input);
									
									if(nuevoID <= 0)
										notificar("El ID debe ser mayor a cero.");
									else {
										// Si el ID no existe, error.
										query = em.createNamedQuery("cliente.porID");
										query.setParameter("id", nuevoID);
										clientes = query.getResultList();
										if(clientes.isEmpty()) {
											notificar("No existe el ID de cliente.");
										} else repetir = false;
									}
								}
						}
						
						nuevoImporte = -1;
						repetir = true;
						while(repetir) {
							System.out.println();
							System.out.println("Importe actual de factura: " + facturas.get(0).getImporte());
	
							System.out.println();
							System.out.print("Tipee el nuevo valor, o presione Enter para no modificar: ");
							input = consola.nextLine();
	
							if(input.isEmpty()) {
								nuevoImporte = -1;
								repetir = false;
							} else
								if(!esDecimal(input))
									notificar("El valor ingresado no es un decimal.");
								else {
									nuevoImporte = Float.parseFloat(input);
									
									if(nuevoImporte < 0)
										notificar("El importe debe ser mayor o igual a cero.");
									else repetir = false;
								}
						}
						
						if (nuevoID == -1 && nuevoImporte == -1) notificar("No hubo modificaciones.");
						else {
							
							facturaModif = new Factura();
							facturaModif.setNro(facturas.get(0).getNro());
							facturaModif.setIdCliente(nuevoID == -1 ? facturas.get(0).getIdCliente() : nuevoID);
							facturaModif.setImporte(nuevoImporte == -1 ? facturas.get(0).getImporte() : nuevoImporte);
							
							System.out.println();
							System.out.println("-- Datos originales --");
							System.out.println(facturas.get(0).toString());
	
							System.out.println();
							System.out.println("-- Datos modificados --");
							System.out.println(facturaModif.toString());
	
							repetir = true;
							while (repetir) {
								System.out.println();
								System.out.print("¿Desea confirmar los cambios? [S/N]: ");
								input = consola.nextLine();
								switch(input) {
								case "s", "S":
								case "n", "N":
									repetir = false;
								break;
								default:
									notificar("Ingresó una opción incorrecta. Vuelva a intentarlo.");
								}
							}
							
							switch(input) {
							case "s", "S":
								try {
									em.getTransaction().begin();
									if(nuevoID != -1) facturas.get(0).setIdCliente(nuevoID);
									if(nuevoImporte != -1) facturas.get(0).setImporte(nuevoImporte);
									em.getTransaction().commit();
									notificar("Se realizaron las modificaciones.");
								} catch (Exception e) {
									e.printStackTrace();
									enterParaContinuar();
								}
							break;
							case "n", "N":
								notificar("Se canceló la modificación.");
							break;
							}
						}
						repetir = true;
					}
				}
			}
		}
	}

	static public boolean esEntero(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	static public boolean esDecimal(String input) {
		try {
			Float.parseFloat(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	static public void notificar(String mensaje) {
		System.out.println();
		System.out.println(mensaje);
		enterParaContinuar();
	}
	
}