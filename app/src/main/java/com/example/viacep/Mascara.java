package com.example.viacep;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class Mascara {
    // Define a máscara padrão para CEP com a estrutura "##.###-###"
    public static String MASCARA_CEP = "##.###-###";

    // Remove todos os caracteres especiais (pontos, traços, barras, etc.) de uma string
    public static String unmask(String s) {
        // Remove os caracteres . (ponto), - (traço), / (barra), ( (parêntese), ) (parêntese), espaço e vírgula da string.
        return s.replaceAll("[.]", "")
                .replaceAll("[-]", "")
                .replaceAll("[/]", "")
                .replaceAll("[(]", "")
                .replaceAll("[)]", "")
                .replaceAll(" ", "")
                .replaceAll(",", "");
    }

    // Verifica se um caractere é um caractere especial (ponto, traço, barras, parênteses, espaços, vírgula)
    public static boolean isASign(char c) {
        // Verifica se o caractere é um dos caracteres especiais definidos.
        if (c == '.' || c == '-' || c == '/' || c == '(' || c == ')' || c == ',' || c == ' ') {
            return true;
        } else {
            return false;
        }
    }

    // Aplica uma máscara a um texto, formatando-o com base na máscara fornecida
    public static String mask(String mask, String text) {
        int i = 0;
        String mascara = "";
        // Itera sobre cada caractere da máscara e do texto
        for (char m : mask.toCharArray()) {
            if (m != '#') {
                // Se o caractere na máscara não for #, mantém o caractere na máscara
                mascara += m;
                continue;
            }
            try {
                // Se o caractere na máscara for #, substitui pelo próximo caractere do texto
                mascara += text.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }
        return mascara;
    }

    // Cria um TextWatcher para aplicar a máscara a um EditText
    public static TextWatcher insert(final String mask, final EditText ediTxt) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            // Este método é chamado quando o texto no EditText é alterado
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Obtém o texto atual, remove a formatação e armazena em 'str'
                String str = Mascara.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    // Se já está atualizando (por causa da formatação), guarda o valor antigo e sai da função
                    old = str;
                    isUpdating = false;
                    return;
                }

                int index = 0;
                for (int i = 0; i < mask.length(); i++) {
                    char m = mask.charAt(i);
                    if (m != '#') {
                        // Se o caractere na máscara não for '#', mantém esse caractere na máscara
                        mascara += m;
                        continue;
                    }

                    try {
                        // Se o caractere na máscara for '#', substitui pelo próximo caractere de 'str'
                        mascara += str.charAt(index);
                    } catch (Exception e) {
                        break;
                    }

                    index++;
                }

                if (mascara.length() > 0) {
                    char last_char = mascara.charAt(mascara.length() - 1);
                    boolean hadSign = false;
                    while (isASign(last_char) && str.length() == old.length()) {
                        // Remove caracteres especiais do final da máscara, se necessário
                        mascara = mascara.substring(0, mascara.length() - 1);
                        last_char = mascara.charAt(mascara.length() - 1);
                        hadSign = true;
                    }

                    if (mascara.length() > 0 && hadSign) {
                        // Se ainda restar um caractere especial, remove também
                        mascara = mascara.substring(0, mascara.length() - 1);
                    }
                }

                // Define que a formatação está sendo aplicada
                isUpdating = true;
                // Define o texto formatado no EditText
                ediTxt.setText(mascara);
                // Move o cursor para o final do texto formatado
                ediTxt.setSelection(mascara.length());
            }

            // Este método é chamado antes do texto ser alterado
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // Este método é chamado após o texto ser alterado
            public void afterTextChanged(Editable s) {}
        };
    }
}
