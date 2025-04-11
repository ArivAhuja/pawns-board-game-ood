package cs3500.pawnsboard.provider.view;


import cs3500.pawnsboard.provider.model.Player;

public interface ModelListener {
  void yourTurn(Player p);
  void refresh();
  void showMessage(String msg);
}

