package br.com.nelsonwilliam.dsp20191.chernobyl.presentation.controller;

import br.com.nelsonwilliam.dsp20191.chernobyl.business.dto.UsuarioDto;
import br.com.nelsonwilliam.dsp20191.chernobyl.business.entity.Usuario;
import br.com.nelsonwilliam.dsp20191.chernobyl.business.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Transactional
@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/usuario/cadastrar")
    public String cadastrar(Model model) {
        model.addAttribute("usuarioDto", new UsuarioDto());
        return "usuario/cadastrar";
    }

    @PostMapping("/usuario/cadastrar/enviar")
    public String cadastrarEnviar(@Valid UsuarioDto usuarioDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "usuario/cadastrar";
        }
        if (usuarioService.findByLogin(usuarioDto.getLogin()) != null) {
            bindingResult.rejectValue("login", "error.usuarioDto", "Já existe um usuário com o login informado.");
            return "usuario/cadastrar";
        }
        if (usuarioService.findByEmail(usuarioDto.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.usuarioDto", "Já existe um usuário com o e-mail informado.");
            return "usuario/cadastrar";
        }

        Usuario usuario = usuarioDto.toUsuario(passwordEncoder);
        usuarioService.save(usuario);
        return "redirect:/usuario/login?created";
    }

    /**
     * NOTA sobre diferentes estados da página de login:
     * Após falhar no login, é redirecionado para "login?error".
     * Após efetuar logout, é redirecionado pra "login?logout".
     * Após cadastrar, é redirecionado para "login?created".
     * Isso tudo é lidado pelo próprio template, checando os parâmetros.
     */
    @GetMapping("/usuario/login")
    public String login() {
        return "usuario/login";
    }

}
