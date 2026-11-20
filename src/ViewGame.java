import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class ViewGame {
	PositionTrace model;
	Board board;

	ViewGame(PositionTrace model) {
		this.model = model;
		board = new Board("Jogo das Damas", model.size(), model.size(), 80);
		board.setIconProvider(this::icon);
		board.setBackgroundProvider(this::background);
		board.addMouseListener(this::click);
		board.addAction("new", this::actionNew);
		board.addAction("random", this::actionRandom);
		board.addAction("gravar", this::actionSave);
		board.addAction("carregar", this::actionLoad);

	}

	String icon(int line, int col) {
		if (model.isPlayerWhite(line, col)) {
			return "white.png";
		}

		if (model.isPlayerBlack(line, col)) {
			return "black.png";
		}

		return null;
	}

	void click(int line, int col) {

		
		
		// Se já houver uma peça selecionada e o movimento é válido
		if (model.isSelected() && model.legalMove(line, col)) {
			model.moveTo(line, col); // Realiza o movimento

		}
		// Se não houver peça selecionada, ou se for a vez de selecionar uma nova peça
		if (model.isPlayerWhite(line, col) && model.currentPlayer()
				|| model.isPlayerBlack(line, col) && !model.currentPlayer()) {
			model.select(line, col); // Seleciona a peça
		}

		// verificar o que se passa
		if (model.endGame()) {
			board.showMessage("Fim!" + model.theWinner());
		}
		
		// Atualiza o título do jogo conforme a vez do jogador
		if (model.currentPlayer()) {
			board.setTitle("Vez do jogador branco");
		} else {
			board.setTitle("Vez do jogador preto");
		}

	}

	Color background(int line, int col) {
		
		if (model.isSelected() && model.isSelectedPosition(line, col)) {
			return StandardColor.YELLOW;
		}

		if (model.blackCases(line, col)) {
			// matriz nessas casas vai ser preta;
			return StandardColor.BLACK;
		} else
			return StandardColor.WHITE;
	}

	void actionNew() {
		int size = board.promptInt("what the size you want?");
		int numPieces = board.promptInt("what the num of pieces you want?");
		if (size > 0 && numPieces > 0) {
			PositionTrace newModel = new PositionTrace(size, numPieces);
			ViewGame gui = new ViewGame(newModel);
			gui.start();
		}
	}

	void actionRandom() {
		if(!model.endGame()) {
			model.random();
			if (model.endGame()) {
				board.showMessage("Fim!" + model.theWinner());
			}
		}
		if (model.currentPlayer()) {
			board.setTitle("Vez do jogador branco");
		} else {
			board.setTitle("Vez do jogador preto");
		}
		if(model.endGame()) {
			board.showMessage("Fim!" + model.theWinner());
		}

	}

	void actionSave() {
		String fileName = board.promptText("What is the file name you want?");
		model.saveGame(fileName);

	}

	void actionLoad() {

		String fileName = board.promptText("What is the file you want?");
		PositionTrace pt = new PositionTrace();
		pt.load(fileName);
		ViewGame gui = new ViewGame(pt);// aqui vai importar o que esta guardado na variavel pt.load para cada vez que
										// carregar-mos um load ele abrir uma nova janeka com esse conteudo
		gui.start();
	}

	void start() {
		board.open();
	}

	public static void main(String[] args) {
		ViewGame gui = new ViewGame(new PositionTrace());
		gui.start();
	}
}
