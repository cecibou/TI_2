package com.ti2cc;
import java.util.Scanner;

public class Principal {

public static void main(String[] args) {
		
		DAO dao = new DAO();
		
		dao.conectar();

		Scanner ler = new Scanner(System.in);
		int opcao = 0;
		X[] usuarios = dao.getUsuarios();
		X usuario = new X();
		//menu de opcoes
		while(opcao != 5) {
			System.out.println("1 - Listar");
			System.out.println("2 - Inserir");
			System.out.println("3 - Excluir");
			System.out.println("4 - Atualizar");
			System.out.println("5 - Sair");
			System.out.println("Escolha o numero de uma das opcoes acima:");
			opcao = ler.nextInt();
			if(opcao == 1){//Mostrar usuários
				usuarios = dao.getUsuarios();
				System.out.println("==== Mostrar usuários === ");		
				for(int i = 0; i < usuarios.length; i++) {
					System.out.println(usuarios[i].toString());
				}
			}else if(opcao == 2) {//Inserir um elemento na tabela
				System.out.println("Digite o codigo:");
				usuario.setCodigo(ler.nextInt());
				System.out.println("Digite o Login:");
				usuario.setLogin(ler.next());
				ler.nextLine();
				System.out.println("Digite a Senha:");
				usuario.setSenha(ler.nextLine());
				System.out.println("Digite o Sexo(M ou F):");
				usuario.setSexo(ler.next().charAt(0));
				if(dao.inserirUsuario(usuario) == true) {
					System.out.println("Inserção com sucesso -> " + usuario.toString());
				}
			}else if(opcao == 3) {//Excluir usuário
				System.out.println("Digite o codigo:");
				usuario.setCodigo(ler.nextInt());
				dao.excluirUsuario(usuario.getCodigo());
			}else if(opcao == 4) {//Atualizar usuário
				String novaSenha;
				System.out.println("Digite a nova senha:");
				novaSenha = ler.next();
				usuario.setSenha(novaSenha);
				dao.atualizarUsuario(usuario);
			}else{
				System.out.println("------- Saindo -------");
			}
		}
		
		//fechar o scanner
		ler.close();
		/*
		//Inserir um elemento na tabela
		//X usuario = new X(11, "pablo", "pablo",'M');
		if(dao.inserirUsuario(usuario) == true) {
			System.out.println("Inserção com sucesso -> " + usuario.toString());
		}
		
		//Mostrar usuários do sexo masculino		
		System.out.println("==== Mostrar usuários do sexo masculino === ");
		//X[] usuarios = dao.getUsuariosMasculinos();
		for(int i = 0; i < usuarios.length; i++) {
			System.out.println(usuarios[i].toString());
		}
		//Atualizar usuário
		usuario.setSenha("nova senha");
		dao.atualizarUsuario(usuario);
		//Mostrar usuários do sexo masculino
		System.out.println("==== Mostrar usuários === ");
		usuarios = dao.getUsuarios();
		for(int i = 0; i < usuarios.length; i++) {
			System.out.println(usuarios[i].toString());
		}
		
		//Excluir usuário
		dao.excluirUsuario(usuario.getCodigo());
		
		//Mostrar usuários
		usuarios = dao.getUsuarios();
		System.out.println("==== Mostrar usuários === ");		
		for(int i = 0; i < usuarios.length; i++) {
			System.out.println(usuarios[i].toString());
		}*/
		
		dao.close();
	}

}