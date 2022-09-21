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
				System.out.println("Opci�n incorrecta. Vuelva a intentarlo.");
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
	
	// Funcionamiento: muestra el men� principal y obtiene
	// la opci�n del usuario. Devuelve "err" si se eligi� una
	// opci�n incorrecta.
	static public String opcionMenu() {
		String input = opError; // Por defecto es opci�n inv�lida.
		
		System.out.println("Men� principal");
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
		System.out.print("Ingrese una opci�n: ");
		
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
	
	// Funcionamiento: muestra el men� de creaci�n de cliente
	// y le indica al usuario que ingrese los datos para su creaci�n.
	// S�lo permite crear clientes que no existan en la BD y cuya
	// descripci�n no est� vac�a. Tambi�n brinda la opci�n de cancelar
	// la creaci�n.
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
			System.out.println("Men� de creaci�n de cliente");
			System.out.println("---------------------------");
			
			repetir = true;
			while(repetir) {
				
				// El usuario ingresa el ID o sale.
				System.out.println();
				System.out.print("Ingrese el ID � 0 para cancelar: ");
				input = consola.nextLine();
				if (input.equals("0")) {
					repetir = false;
					volver = true;
					
				} else { 
					try {
						
						// Si el ID no es v�lido, error.
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
								
									// El usuario ingresa la descripci�n o sale.
									System.out.println();
									System.out.print("Ingrese la descripci�n � 0 para cancelar: ");
									input = consola.nextLine();
									if (input.equals("0")) {
										repetir = false;
										volver = true;
									
									// Si la descripci�n est� vac�a, error.
									} else if (input.isEmpty()) {
										System.out.println();
										System.out.println("La descripci�n no puede estar vac�a.");
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
												System.out.print("�Desea ingresar otro cliente? [S/N]: ");
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
													System.out.println("Ingres� una opci�n incorrecta. Vuelva a intentarlo.");
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
						System.out.println("El ID debe ser num�rico.");
						enterParaContinuar();
					}
				}
			}
		}
	}

	// Funcionamiento: muestra el men� de creaci�n de cliente
	// y le indica al usuario que ingrese los datos para su creaci�n.
	// S�lo permite crear clientes que no existan en la BD y cuya
	// descripci�n no est� vac�a. Tambi�n brinda la opci�n de cancelar
	// la creaci�n.
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
			System.out.println("Men� de creaci�n de factura");
			System.out.println("---------------------------");
			
			repetir = true;
			while(repetir) {
				
				// El usuario ingresa el n�mero o sale.
				System.out.println();
				System.out.print("Ingrese el n�mero � 0 para cancelar: ");
				input = consola.nextLine();
				if (input.equals("0")) {
					repetir = false;
					volver = true;
					
				} else { 
					try {
						
						// Si el n�mero ingresado no es v�lido, error.
						numeroFactura = Integer.parseInt(input);
						if (numeroFactura < 0) {
							System.out.println();
							System.out.println("El n�mero debe ser mayor a cero.");
							enterParaContinuar();
							
						} else {
							
							// Si el n�mero ya existe, error.
							query = em.createNamedQuery("factura.porNro");
							query.setParameter("nro", numeroFactura);
							facturas = query.getResultList();
							if(!facturas.isEmpty()) {
								System.out.println();
								System.out.println("Ya existe el n�mero de factura.");
								enterParaContinuar();
								
							} else {
								
								repetir = true;
								while(repetir) {
									
									// El usuario ingresa el ID de cliente o sale.
									System.out.println();
									System.out.print("Ingrese el ID de cliente � 0 para cancelar: ");
									input = consola.nextLine();
									if (input.equals("0")) {
										repetir = false;
										volver = true;
										
									} else {
										try {
											
											// Si el ID ingresado no es v�lido, error.
											codigoCliente = Integer.parseInt(input);
											if (codigoCliente < 0) {
												System.out.println();
												System.out.println("El c�digo de cliente debe ser mayor a cero.");
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
														
														// El usuario ingresa el importe � sale.
														System.out.println();
														System.out.print("Ingrese el importe de la factura � 0 para cancelar: ");
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
																			System.out.print("�Desea ingresar otra factura? [S/N]: ");
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
																				System.out.println("Ingres� una opci�n incorrecta. Vuelva a intentarlo.");
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
																System.out.println("El importe debe ser num�rico.");
																enterParaContinuar();
															}
														}
													} // while
												}
											}
										} catch (NumberFormatException e) {
											System.out.println();
											System.out.println("El ID debe ser num�rico.");
											enterParaContinuar();
										}
									}
								} // while
							}
						}
					} catch (NumberFormatException e) {
						System.out.println();
						System.out.println("El ID debe ser num�rico.");
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
					System.out.println("El c�digo debe ser num�rico. Vuelva a intentarlo.");
					enterParaContinuar();
				}
			}
		}
	}

	// Funcionamiento: le pregunta al usuario si quiere enlistar
	// todas las facturas o si quiere mostrar una factura en base
	// a su n�mero.
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
			System.out.print("Ingrese el n�mero de la factura a mostrar o presione Enter para mostrar todas: ");
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
					
					// Si el n�mero es incorrecto, error.
					numeroFactura = Integer.parseInt(input);
					if(numeroFactura <= 0) {
						System.out.println();
						System.out.println("El n�mero debe ser mayor a cero. Vuelva a intentarlo.");
						
					} else {
						
						// Si no hay factura con el n�mero, avisa.
						query = em.createNamedQuery("factura.porNro");
						query.setParameter("nro", numeroFactura);
						facturas = query.getResultList();
						if(facturas.isEmpty()) {
							System.out.println();
							System.out.println("No hay factura con el n�mero " + numeroFactura + ".");
							
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
					System.out.println("El valor debe ser num�rico. Vuelva a intentarlo.");
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
			System.out.println("Eliminaci�n de clientes");
			System.out.println("-----------------------");
			
			repetir = true;
			while(repetir) {
				
				// El usuario ingresa el ID o sale.
				System.out.println();
				System.out.print("Ingrese el ID � 0 para cancelar: ");
				input = consola.nextLine();
				if (input.equals("0")) {
					repetir = false;
					volver = true;
					
				} else { 
					try {
						
						// Si el ID no es v�lido, error.
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
												System.out.print("�Desea dar de Baja otro cliente? [S/N]: ");
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
													System.out.println("Ingres� una opci�n incorrecta. Vuelva a intentarlo.");
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
						System.out.println("El ID debe ser num�rico.");
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
				
				// El usuario ingresa el n�mero de factura o sale.
				System.out.println();
				System.out.print("Ingrese el n�mero de factura � 0 para cancelar: ");
				input = consola.nextLine();
				if (input.equals("0")) {
					repetir = false;
					volver = true;
					
				} else { 
					try {
						
						// Si el n�mero de factura no es v�lido, error.
						codigoFactura = Integer.parseInt(input);
						if (codigoFactura < 0) {
							System.out.println();
							System.out.println("El n�mero de factura debe ser mayor a cero.");
							enterParaContinuar();
						}

						else {
							
							// Si el n�mero de factura no existe, error.
							query = em.createNamedQuery("factura.porNro");
							query.setParameter("nro", codigoFactura);
							facturas = query.getResultList();
							
							if(facturas.isEmpty()) {
								System.out.println();
								System.out.println("No existe el n�mero de factura.");
								enterParaContinuar();

							} else {
								repetir = true;
								while(repetir) {
									// Eliminamos a la factura.
									// Como la lista no est� vac�a, existe la factura.
									try {
										em.getTransaction().begin();
										em.remove(facturas.get(0));
										em.getTransaction().commit();
										System.out.println();
										System.out.println("Factura eliminada.");
										
										while (repetir) {
											System.out.println();
											System.out.print("�Desea dar de baja otra factura? [S/N]: ");
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
												System.out.println("Ingres� una opci�n incorrecta. Vuelva a intentarlo.");
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
						System.out.println("El valor debe ser num�rico.");
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
			System.out.println("Modificaci�n de clientes");
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
						System.out.println("Descripci�n actual: " + clientes.get(0).getDescr());

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
								System.out.print("�Desea confirmar los cambios? [S/N]: ");
								input = consola.nextLine();
								switch(input) {
								case "s", "S":
								case "n", "N":
									repetir = false;
								break;
								default:
									notificar("Ingres� una opci�n incorrecta. Vuelva a intentarlo.");
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
								notificar("Se cancel� la modificaci�n.");
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
			System.out.println("Modificaci�n de factura");
			System.out.println("-----------------------");
			
			System.out.print("Ingrese el n�mero de factura a modificar, o 0 para volver: ");
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
						notificar("No existe la factura con n�mero " + numeroFactura);
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
								System.out.print("�Desea confirmar los cambios? [S/N]: ");
								input = consola.nextLine();
								switch(input) {
								case "s", "S":
								case "n", "N":
									repetir = false;
								break;
								default:
									notificar("Ingres� una opci�n incorrecta. Vuelva a intentarlo.");
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
								notificar("Se cancel� la modificaci�n.");
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