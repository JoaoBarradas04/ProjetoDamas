
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;

record Position(int line, int col) {

}// cria me um obejto de forma a poder mexer com as linhas e colunas disponiveis
	// no tabuleiro

public class PositionTrace {

	private final int emptyPieces = 0;
	private final int whitePieces = 1;
	private final int blackPieces = 2;

	private boolean currentPlayer = true; // vez do jogador branco
	private int[][] matrix;
	private int size;

	private Position selectedPosition; //Posição da peça selecionada para realizar movimentos
	
	public PositionTrace() {
		this(8, 12);
	}

//construtor para a jogo funcionar com diferentes tamanhos
	public PositionTrace(int givenSize, int givenNumPieces) {
		size = givenSize;
		matrix = new int[size][size];//cria um novo tab com o tamanho quadrado como quere-mos
		setPieces(givenNumPieces);// coloquei aqui para o jogo ao iniciar ja com as peças colocadas
	}

	public boolean currentPlayer() {
		return currentPlayer;
	}

	public boolean blackCases(int line, int col) {
		// função criada para certificar me do numero de casas pretas.
		return (line + col) % 2 != 0;
	}

	public boolean isPlayerWhite(int line, int col) {

		return matrix[line][col] == whitePieces;
	}

	public boolean isPlayerBlack(int line, int col) {
		return matrix[line][col] == blackPieces;
	}

	public int size() {
		return matrix.length;//indica-nos o numero de linhas de uma matriz que neste caso tem sempre de ser igual ao numero
		//de colunas.
	}

	public void setPieces(int numPieces) {
		// vou necessitar de um count, menor que numPieces.
		// dois for para percorrer as linhas e colunas.
		// so colocar quando e blackCases.
		int count = 0;// contador de peças colocadas

		// peças pretas
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size && count < numPieces; j++) {
				if (blackCases(i, j)) {
					matrix[i][j] = blackPieces;
					count++;
				}
			}
		}
		// o count aquui foi implementado para "resetar" o mesmo a 0 de forma a garantir
		// as mesmas peças brancas e pretas(se possivel questionar o professor sobre)
		count = 0;
		// peças brancas
		for (int i = size - 1; i >= 0; i--) {
			for (int j = size - 1; j >= 0 && count < numPieces; j--) {
				if (blackCases(i, j)) {
					matrix[i][j] = whitePieces;
					count++;
				}
			}
		}
	}

	public boolean legalMove(int line, int col) {

//garante que a linha selecionada nao vai para fora do tab.
		if (line >= size || col >= size || line < 0 || col < 0) {
			return false;
		}
//garante que a a casa selecioanda tem de estar vazia
		if (matrix[line][col] != 0) {
			return false;
		}

		if (selectedPosition == null) {
			return false; // Nenhuma peça foi selecionada
		}
//funçao definida para podermos executar o movimento desde onde esta a peça("startLine" e "startCol") para a posiçao em questao 
		int startLine = selectedPosition.line();
		int startCol = selectedPosition.col();

		// Verifica se há capturas disponíveis para o jogador atual, foi feito apenas
		// para simplficar pois se eu invocasse a função diretamente no if o mesmo
		// funcionaria

		boolean captureRequired = anyAvailableCapture();

		if (captureRequired) {
			// Se a captura é obrigatória, apenas capturas serão permitidas
			return isCaptureMove(line, col);
		}

		// Movimentos normais (apenas se não houver capturas disponíveis)
		// Movimento para peça branca e começa por verificar se a mesma é uma peça
		// branca
		if (isPlayerWhite(startLine, startCol) && currentPlayer) {
			// Diagonal direita
			if (line == startLine - 1 && col == startCol + 1 && blackCases(line, col)) {
				return true;

			}
			// Diagonal esquerda
			if (line == startLine - 1 && col == startCol - 1 && blackCases(line, col)) {
				return true;
			}
		}

		// Movimento para peça preta("espelho da peça branca")
		if (isPlayerBlack(startLine, startCol) && !currentPlayer) {
			// Diagonal direita
			if (line == startLine + 1 && col == startCol + 1 && blackCases(line, col)) {
				return true;
			}
			// Diagonal esquerda
			if (line == startLine + 1 && col == startCol - 1 && blackCases(line, col)) {
				return true;
			}
		}

		return false; // Caso nenhuma condição seja satisfeita
	}

//implementar o legalmove de forma a poder mover as peças coisa que nao esta a acontecer!
	public void moveTo(int line, int col) {
		// Verifica se há uma peça selecionada e se o movimento é legal
		if (selectedPosition != null && legalMove(line, col)) {
			int startLine = selectedPosition.line();
			int startCol = selectedPosition.col();

			// Captura
			if (line == startLine - 2 || line == startLine + 2) {
				// Verificar se a coluna também foi alterada de forma correta quando formos
				// fazer a captura
				if (col == startCol - 2 || col == startCol + 2) {
					int capturedLine = (startLine + line) / 2;
					int capturedCol = (startCol + col) / 2;
					matrix[capturedLine][capturedCol] = 0; // Remove a peça capturada
				}
			}

			// Move a peça para a nova posição
			if (currentPlayer) { // Jogador branco
				matrix[line][col] = whitePieces;
			} else { // Jogador preto
				matrix[line][col] = blackPieces;
			}

			// Limpa a posição inicial
			matrix[startLine][startCol] = 0;

			// Verifica se a peça atingiu a extremidade e a bloqueia a mesma
			if (isPieceBlocked(line, col)) {
				// Não fazemos mais nada aqui pois a peça ao chegar a esta posiçao ficara
				// bloqueada, apenas não permitimos movimento para essa peça
				//dai nao retornar nada.
			}

			// Reseta a seleção de forma a depois podermos escolher outra peça apos a jogada
			// da posiçao anterior
			selectedPosition = null;

			// Passa a vez para o próximo jogador
			currentPlayer = !currentPlayer;
		}
	}

//este certifica se que a posição selecionada nao é nula
	boolean isSelected() {
		return selectedPosition != null;
	}

//vai certifica se de da posiçao que ira selecionar sera a nova posiçao da suposta peça  
	boolean isSelectedPosition(int line, int col) {
		return selectedPosition != null && selectedPosition.line() == line && selectedPosition.col() == col;
	}

//procedimento que vai servir como base 
	void select(int line, int col) {
		// Se a captura for obrigatória, só pode selecionar as peças que têm capturas
		// disponíveis
		boolean captureRequired = anyAvailableCapture();
//vai certificar se de executar o que foi definido anteriormente.
		if (captureRequired) {
			if (hasAvailableCapture(line, col)
					&& ((currentPlayer && isPlayerWhite(line, col)) || (!currentPlayer && isPlayerBlack(line, col)))) {
				selectedPosition = new Position(line, col);
			}
		} else {
			// Se não há capturas obrigatórias, qualquer peça válida pode ser selecionada
			if ((currentPlayer && isPlayerWhite(line, col)) || (!currentPlayer && isPlayerBlack(line, col))) {
				selectedPosition = new Position(line, col);
			}
		}
	}

//analisa todas as capturas possiveis 
	private boolean hasAvailableCapture(int line, int col) {
		// Captura para peça branca
		if (currentPlayer && isPlayerWhite(line, col)) {
			// Diagonal direita superior
			if (line - 2 >= 0 && col + 2 < size && blackCases(line - 2, col + 2)
					&& matrix[line - 1][col + 1] == blackPieces && matrix[line - 2][col + 2] == 0) {
				return true;
			}
			// Diagonal esquerda superior
			if (line - 2 >= 0 && col - 2 >= 0 && blackCases(line - 2, col - 2)
					&& matrix[line - 1][col - 1] == blackPieces && matrix[line - 2][col - 2] == 0) {
				return true;
			}
		}

		// Captura para peça preta
		if (!currentPlayer && isPlayerBlack(line, col)) {
			// Diagonal direita inferior
			if (line + 2 < size && col + 2 < size && blackCases(line + 2, col + 2)
					&& matrix[line + 1][col + 1] == whitePieces && matrix[line + 2][col + 2] == 0) {
				return true;
			}
			// Diagonal esquerda inferior
			if (line + 2 < size && col - 2 >= 0 && blackCases(line + 2, col - 2)
					&& matrix[line + 1][col - 1] == whitePieces && matrix[line + 2][col - 2] == 0) {
				return true;
			}
		}

		return false;
	}

//obriga a captura disponivel 
	private boolean isCaptureMove(int line, int col) {
		int startLine = selectedPosition.line();
		int startCol = selectedPosition.col();

		// Captura para peça branca
		if (currentPlayer && isPlayerWhite(startLine, startCol)) {
			// Verifica a diagonal direita superior
			if (line == startLine - 2 && col == startCol + 2) {
				int capturedLine = startLine - 1; // Posição intermediária (linha)
				int capturedCol = startCol + 1; // Posição intermediária (coluna)
				return matrix[capturedLine][capturedCol] == blackPieces && matrix[line][col] == 0;
			}
			// Verifica a diagonal esquerda superior
			if (line == startLine - 2 && col == startCol - 2) {
				int capturedLine = startLine - 1; // Posição intermediária (linha)
				int capturedCol = startCol - 1; // Posição intermediária (coluna)
				return matrix[capturedLine][capturedCol] == blackPieces && matrix[line][col] == 0;
			}
		}

		// Captura para peça preta
		if (!currentPlayer && isPlayerBlack(startLine, startCol)) {
			// Verifica a diagonal direita inferior
			if (line == startLine + 2 && col == startCol + 2) {
				int capturedLine = startLine + 1; // Posição intermediária (linha)
				int capturedCol = startCol + 1; // Posição intermediária (coluna)
				return matrix[capturedLine][capturedCol] == whitePieces && matrix[line][col] == 0;
			}
			// Verifica a diagonal esquerda inferior
			if (line == startLine + 2 && col == startCol - 2) {
				int capturedLine = startLine + 1; // Posição intermediária (linha)
				int capturedCol = startCol - 1; // Posição intermediária (coluna)
				return matrix[capturedLine][capturedCol] == whitePieces && matrix[line][col] == 0;
			}
		}

		return false;

	}
//verifica a captura para qualquer das peças 
	private boolean anyAvailableCapture() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if ((currentPlayer && isPlayerWhite(i, j)) || (!currentPlayer && isPlayerBlack(i, j))) {
					if (hasAvailableCapture(i, j)) {
						return true; // Há uma captura possível
					}
				}
			}
		}
		return false; // Nenhuma captura disponível
	}
//---------------------------------------------------Fim Capturas-------------------------------------------

//peça bloqueada quando chega ao fim
	public boolean isPieceBlocked(int line, int col) {
		if (isPlayerWhite(line, col)) {
			return line == 0; // Se for peça branca, ela está bloqueada se chegar à linha 0
		}
		if (isPlayerBlack(line, col)) {
			return line == size - 1; // Se for peça preta, ela está bloqueada se chegar à última linha
		}
		return false; // Caso não seja nenhuma peça válida
	}

//Contar peças brancas disponiveis e com jogadas possiveis
	public int countWhitePieces() {
		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (matrix[i][j] == whitePieces) {
					count++;
				}
			}
		}
		return count;
	}

//Contar peças pretas
	public int countBlackPieces() {
		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (matrix[i][j] == blackPieces) {
					count++;

				}
			}
		}
		return count;
	}

//fim do jogo por movimentos

	private boolean hasLegalMove(int line, int col) {
		// Verifica se a peça selecionada e tem de estar selecionada nesta posição pode realizar um movimento legal
		// Verifica movimentos para a direita e esquerda para as duas cores primeiro
		// para as peças brancas e depois para as peças pretas
		if (legalMove(line - 1, col - 1) || legalMove(line - 1, col + 1) || legalMove(line + 1, col - 1)
				|| legalMove(line + 1, col + 1)) {
			return true; // Se houver qualquer movimento legal, retorna verdadeiro
		}
		return false; // Caso contrário, retorna falso
	}

	public boolean hasAnyMove() {
		 
		if (selectedPosition == null) {
			return true;
		} 
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (hasLegalMove(i, j)) {
					return true; // Se encontrar uma jogada possível, retorna verdadeiro
				}
			}
		}

		return false; // Se não encontrar nenhuma jogada, retorna falso
	}

	public boolean endGame() {
		int whiteCount = countWhitePieces();
		int blackCount = countBlackPieces();

		if (whiteCount == 0 || blackCount == 0) {
			return true;
//
		} else if (!hasAnyMove()) {
			return true;
		}
		return false;
	}

	public String theWinner() {
		if (endGame()) {
			int whiteCount = countWhitePieces();
			int blackCount = countBlackPieces();
			if (whiteCount == 0) {
				return ("O vencedor são as peças pretas");
			} else if (blackCount == 0) {
				return ("O vencedor são as peças brancas");
			} else if (!hasAnyMove()) {
				if (whiteCount > blackCount) {
					return ("O vencedor são as peças brancas");
				} else if (whiteCount < blackCount) {
					return ("O vencedor são as peças pretas");
				}
			} else {
				return ("Empate");
			}
		}
		return null;// o jogo não vai terminar enquanto nao encontrar uma das hipoteses anteriores.
	}

//------------------------------------------------------Random-------------------------------------
//aviso
	// A função assume que vai existir sempre movimentos válidos 
	//para uma peça escolhida devido ao que acontece no meu haslegalmove no primeiro if.
	// Se nenhum movimento for possível, o loop while pode continuar
	// indefinidamente. Isto pois o meu endgame esta cnstruido para terminar com a seleção de uma peça 
	//como ja foi explicado dai dar me o erro do random "infinito"
	public void random() {
		 
		boolean positionValid = false;// começa em falso pois so passa para verdadeiro ao encontrar uma jogada valida
		int selctLine;
		int selctCol;
		
		//vai procurar posições validas e fazer mover as peças 
		while (!positionValid) {
			//vai executar a operaçao multiplicando valores double desde (0,0 ate 0,9) pelo size e o resultado vai converter para inteiro
			//os parenteses no int sao obrigatorios pois sem eles ele nao ira converter o resultado
			selctLine = (int) (Math.random() * size);
			selctCol = (int) (Math.random() * size);
			//verifica que a peça a ser selecionada aleatoriamente e uma peça branca  
			if ((currentPlayer && matrix[selctLine][selctCol] == whitePieces)) {
				select(selctLine, selctCol);
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						if (legalMove(i, j)) {
							positionValid = true;
							moveTo(i, j);

						}
					}
				}
			}
			if ((!currentPlayer && matrix[selctLine][selctCol] == blackPieces)) {
				select(selctLine, selctCol);
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						if (legalMove(i, j)) {
							positionValid = true;
							moveTo(i, j);

						}
					}
				}
			}

		}
		}

	

//-----------------------------------------------------------------Guardar e Carregar o Jogo------------------------------------------

	public void saveGame(String fileName) {
		try {
			// Criação do PrintWriter para escrever no ficheiro
			PrintWriter writer = new PrintWriter(new File(fileName));// objeto importado
			writer.println(size);
			writer.println(currentPlayer);

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (matrix[i][j] != emptyPieces)
						writer.println(matrix[i][j] + " " + i + " " + j); // Escreve cada peça no tabuleiro

				}
				// Nova linha para a próxima linha do tabuleiro
			}

			writer.close();
			System.out.println("Gravado com sucesso");
		} catch (FileNotFoundException e) {
			System.err.println("Erro ao tentar salvar o jogo no ficheiro.");
		}
	}

	void load(String fileName) {
		try {
			Scanner scanner = new Scanner(new File(fileName));
			size = scanner.nextInt(); // aqui temos de ir buscar o size guardado e nao um novo inteiro
			// pois se criarmos uma var para um novo size ele vai buscar a do tamanho do
			// atual tabuleiro ao invez do que foi guardado 
			matrix = new int[size][size];//cria um tab com o tamanho guardado anteriormente 
			currentPlayer = scanner.nextBoolean();//começa com o jogador da vez em que foi guardado
			while (scanner.hasNext()) {//enquanto existir dados para guardar ele vai procurar 
				int player = scanner.nextInt();//o jogador 
				int line = scanner.nextInt();// onde estao as peças numa det line ou
				int col = scanner.nextInt();//col
				matrix[line][col] = player;//e a posiçao das peças desse jogador 

			}
			scanner.close();
			System.out.println("Carregado com sucesso");
		} catch (FileNotFoundException e) {
			System.err.println("Erro ao tentar carregar o jogo do ficheiro.");
		}
	}

}