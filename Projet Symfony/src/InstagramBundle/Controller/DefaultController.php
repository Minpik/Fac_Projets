<?php

namespace InstagramBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use InstagramBundle\Entity\User;
use InstagramBundle\Entity\Publication;
use InstagramBundle\Entity\Commentaire;
use InstagramBundle\Entity\Jaime;
use InstagramBundle\Entity\Message;
use InstagramBundle\Form\UserType;
use InstagramBundle\Form\PublicationType;
use InstagramBundle\Form\CommentaireType;
use InstagramBundle\Form\MessageType;
use Symfony\Component\Form\Extension\Core\Type\FormType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\JsonResponse;

class DefaultController extends Controller
{
    public function homeAction()
    {
        // Si le visiteur n'est pas identifié, on redirige vers la page d'inscription
        if (!$this->get('security.authorization_checker')->isGranted('IS_AUTHENTICATED_REMEMBERED')) {
	        return $this->redirectToRoute('instagram_inscription');
        }
        
        $me = $this->getUser();
        
        // on récupère les publications des personnes que l'on suit
        $repository = $this->getDoctrine()
            ->getManager()
            ->getRepository('InstagramBundle:Publication')
        ;
        $abonnementsId = [];
        foreach ($me->getAbonnements() as $a) {
            $abonnementsId[] = $a->getId();
        }
        if (count($abonnementsId) > 0)
            $publications = $repository->findBy((array("user" => $abonnementsId)), array("id" => "DESC"), 5, 0);
        else
            $publications = null;

        return $this->render("InstagramBundle:Default:home.html.twig", array(
            "publications"  => $publications
        ));
    }
    
    public function creditsAction(Request $request)
    {
        return $this->render("InstagramBundle:Default:credits.html.twig");
    }
    
    public function inscriptionAction(Request $request)
    {
        // On crée un objet User
		$user = new User();

		// On crée le formulaire
		$form = $this->createForm(UserType::class, $user);
        
        if ($request->isMethod('POST')) {
			$form->handleRequest($request);

			// On vérifie que les valeurs entrées sont correctes
			if ($form->isValid()) {
                $factory = $this->get('security.encoder_factory');
                $encoder = $factory->getEncoder($user);

                // Codage du password avec sha512
                $encodedPassword = $encoder->encodePassword($user->getPassWord(), $user->getSalt());
                $user->setPassword($encodedPassword);
                
                // On met l'username en minuscule
                //$user->setUsername(strtolower($user->getUsername()));
                
				// On enregistre notre objet $user dans la base de données
				$em = $this->getDoctrine()->getManager();
				$em->persist($user);
				$em->flush();

				$request->getSession()->getFlashBag()->add('good_notice', 'Merci pour votre inscription.');

				// On redirige vers la page de connexion
				return $this->redirectToRoute("login");
			}
    	}
        
        return $this->render("InstagramBundle:Default:inscription.html.twig", array(
		    "form" => $form->createView()
	    ));
    }
    
    public function connexionAction(Request $request)
    {   
        $authenticationUtils = $this->get('security.authentication_utils');
        return $this->render("InstagramBundle:Default:connexion.html.twig", array(
            'last_username' => $authenticationUtils->getLastUsername(),
            'error'         => $authenticationUtils->getLastAuthenticationError(),
        ));
    }
       
    public function changerMdpAction(Request $request)
    { 
        // Si le visiteur n'est pas identifié, on redirige vers la page d'inscription
        if (!$this->get('security.authorization_checker')->isGranted('IS_AUTHENTICATED_REMEMBERED')) {
	        return $this->redirectToRoute('instagram_inscription');
        }
        
        // On crée un tableau
		$datas = array();

		// On crée le formulaire
		$formBuilder = $this->get('form.factory')->createBuilder(FormType::class, $datas);

		// On ajoute les champs du tableau
		$form = $formBuilder
			->add("password",       PasswordType::class)
            ->add("newpassword",    PasswordType::class)
			->add("changer",        SubmitType::class)
			->getForm()
		;
        
        if ($request->isMethod('POST')) {
			$form->handleRequest($request);

			// On vérifie que les valeurs entrées sont correctes
			if ($form->isSubmitted() && $form->isValid()) {
                $datas = $form->getData();
                $user = $this->getUser();
                
                $factory = $this->get('security.encoder_factory');
                $encoder = $factory->getEncoder($user);

                // Codage du password avec sha512
                $datas["password"] = $encoder->encodePassword($datas["password"], $user->getSalt());

                if ($user->getPassword() == $datas["password"]) {
                    // Codage du newpassword avec sha512
                    $datas["newpassword"] = $encoder->encodePassword($datas["newpassword"], $user->getSalt());
                    $user->setPassword($datas["newpassword"]);
                
                    // On enregistre notre objet $user dans la base de données, par exemple
                    $em = $this->getDoctrine()->getManager();
                    $em->persist($user);
                    $em->flush();

				    $request->getSession()->getFlashBag()->add('good_notice', 'Mot de passe changé.');
                    
                    // On redirige vers l'acceuil
                    return $this->redirectToRoute('instagram_home');
                } else
                	$request->getSession()->getFlashBag()->add('bad_notice', 'Mot de passe incorrect.');
			}
    	}
        
        return $this->render("InstagramBundle:Default:changerMdp.html.twig", array(
            "form"   => $form->createView(),
        ));
    }
    
    public function profilAction($username, Request $request)
    {
        // On crée un objet Publication
		$publication = new Publication();

		// On crée le formulaire
		$form_publication = $this->createForm(PublicationType::class, $publication);
        
        $me = $this->getUser();
        
        if ($request->isMethod('POST')) {
            $form_publication->handleRequest($request);
           
            if ($form_publication->isSubmitted()) {
                 //----------PUBLICATION----------//

                // On vérifie que les valeurs entrées sont correctes
                if ($form_publication->isValid()) {
                    $file = $publication->getImage();

                    $extension = "." . $file->guessExtension();

                    // On enregistre notre objet $publication dans la base de données
                    $em = $this->getDoctrine()->getManager();
                    $publication->setImage($extension);
                    $publication->setUser($me);
                    $em->persist($publication);
                    $em->flush();
                    
                    // on déplace le fichier
                    $file->move(__DIR__.'/../../../web/uploads/publications', $publication->getId() . $extension);

                    $request->getSession()->getFlashBag()->add('good_notice', 'Nouvelle publication éffectuée.');
                }
            }

            $file = $request->files->get('file');
            if ($file) {
                //----------UPLOAD AVATAR----------//
                $extension = "." . $file->guessExtension();

                $file->move(__DIR__.'/../../../web/uploads/avatars', $me->getId() . $extension);

                $me->setAvatar($extension);

                // On enregistre notre objet $user dans la base de données
                $em = $this->getDoctrine()->getManager();
                $em->flush();
            }
            
            return $this->redirectToRoute("instagram_profil", array(
                "username" => $username
            ));
        }
        
        $repository = $this->getDoctrine()
            ->getManager()
            ->getRepository('InstagramBundle:User')
        ;
        $user = $repository->findOneByUsername($username);
        
        if ($user != null) {
            $avatarPath = "uploads/avatars/";
            $avatarPath .= ($user->getAvatar() != "") ? $user->getId() . $user->getAvatar() : "default.png";

            $em = $this->getDoctrine()->getManager();
            
            // On récupère les stats d'un profil
            $nbrPublications = $em->getRepository('InstagramBundle:Publication')->nbrPublications($user->getId());
            $nbrAbonnes = $em->getRepository('InstagramBundle:User')->nbrAbonnes($user->getId());
            $nbrSuivis = $em->getRepository('InstagramBundle:User')->nbrSuivis($user->getId());
            $publications = $em->getRepository('InstagramBundle:Publication')->findBy((array("user" => $user->getId())), array("id" => "DESC"), 12, 0);
            
            if ($me != null) {
                $abonne = $em->getRepository('InstagramBundle:User')->isAbonne($me->getId(), $user->getId());
            } else
                $abonne = false;
            
            return $this->render("InstagramBundle:Default:profil.html.twig", array(
                "user"              => $user,
                "avatar"            => $avatarPath,
                "abonne"            => $abonne,
                "nbrPublications"   => $nbrPublications,
                "nbrAbonnes"        => $nbrAbonnes,
                "nbrSuivis"         => $nbrSuivis,
                "publications"      => $publications,
                "form"              => $form_publication->createView()
            ));   
        }
        
        // aucun argument donc ça affichera une erreure
        return $this->render("InstagramBundle:Default:profil.html.twig");                     
    }
    
    public function commenterAction(Request $request)
    {
        // Si le visiteur n'est pas identifié, on redirige vers la page d'inscription
        if (!$this->get('security.authorization_checker')->isGranted('IS_AUTHENTICATED_REMEMBERED')) {
	        return $this->redirectToRoute('instagram_inscription');
        }
        
        // On crée un objet Commentaire
		$commentaire = new Commentaire();
		$form_commentaire = $this->createForm(CommentaireType::class, $commentaire);
        
        if ($request->isMethod('POST')) {
            $form_commentaire->handleRequest($request);
            
            if ($form_commentaire->isSubmitted()) {
                // On vérifie que les valeurs entrées sont correctes
                if ($form_commentaire->isValid()) {
                    // on récupère la publication que l'on veut commenter
                    $repository = $this->getDoctrine()
                        ->getManager()
                        ->getRepository('InstagramBundle:Publication')
                    ;
                    $publication = $repository->find($request->request->get("idPublication"));

                    // On enregistre notre objet $commentaire dans la base de données
                    $em = $this->getDoctrine()->getManager();
                    $commentaire->setPublication($publication);
                    $commentaire->setUser($this->getUser());
                    $em->persist($commentaire);
                    $em->flush();

                    $request->getSession()->getFlashBag()->add('good_notice', 'Publication commentée.');
                }
            }
        }
        
        return $this->redirectToRoute('instagram_home');
    }
    
    public function sabonnerAction(Request $request)
    {
        // Si le visiteur n'est pas identifié, on redirige vers la page d'inscription
        if (!$this->get('security.authorization_checker')->isGranted('IS_AUTHENTICATED_REMEMBERED')) {
	        return $this->redirectToRoute('instagram_inscription');
        }
        
        if ($request->isMethod('POST')) {
            $username = $request->request->get("username");
            
            $em = $this->getDoctrine()->getManager();

            $suivi = $em->getRepository('InstagramBundle:User')->findOneByUsername($username);
            $this->getUser()->addAbonnement($suivi);

            $em->flush();
                    
            return $this->redirectToRoute("instagram_profil", array(
                "username" => $username
            ));
        }
        
        return $this->redirectToRoute("instagram_home");
    }
    
    public function desabonnerAction(Request $request)
    {
        // Si le visiteur n'est pas identifié, on redirige vers la page d'inscription
        if (!$this->get('security.authorization_checker')->isGranted('IS_AUTHENTICATED_REMEMBERED')) {
	        return $this->redirectToRoute('instagram_inscription');
        }
        
        if ($request->isMethod('POST')) {
            $username = $request->request->get("username");
            
            $em = $this->getDoctrine()->getManager();

            $suivi = $em->getRepository('InstagramBundle:User')->findOneByUsername($username);
            $this->getUser()->removeAbonnement($suivi);

            $em->flush();
                    
            return $this->redirectToRoute("instagram_profil", array(
                "username" => $username
            ));
        }
        
        return $this->redirectToRoute("instagram_home");
    }
    
    public function jaimeAction(Request $request)
    {
        // Si le visiteur n'est pas identifié, on redirige vers la page d'inscription
        if (!$this->get('security.authorization_checker')->isGranted('IS_AUTHENTICATED_REMEMBERED')) {
	        return $this->redirectToRoute('instagram_inscription');
        }

        if ($request->isMethod("POST")) {
            $id = $request->request->get("id");
            if ($id) {
                // on récupère la publication
                $repository = $this->getDoctrine()
                    ->getManager()
                    ->getRepository('InstagramBundle:Publication')
                ;
                $publication = $repository->find($id);
                
                $jaime = new Jaime();
                $jaime->setPublication($publication);
                $jaime->setUser($this->getUser());
                
                $em = $this->getDoctrine()->getManager();
                $em->persist($jaime);
                $em->flush();
                
                return $this->redirectToRoute("instagram_profil", array(
                    "username" => $publication->getUser()->getUsername()
                ));
            }
        }
        
        return $this->redirectToRoute("instagram_home");
    }
    
    public function messagerieAction(Request $request)
    {
        // Si le visiteur n'est pas identifié, on redirige vers la page d'inscription
        if (!$this->get('security.authorization_checker')->isGranted('IS_AUTHENTICATED_REMEMBERED')) {
	        return $this->redirectToRoute('instagram_inscription');
        }
        
        // On crée un objet Message
        $message = new Message();
        $form = $this->createForm(MessageType::class, $message);
        
        $em = $this->getDoctrine()->getManager();
        
        if ($request->isMethod("POST")) {
            $form->handleRequest($request);
            
            if ($form->isSubmitted()) {
                $destinataire = $em->getRepository('InstagramBundle:User')->findOneBy(array("username" => $form->get("destinataire")->getData()));
                
                if ($destinataire) {
                    $message->setExpediteur($this->getUser());
                    $message->setDestinataire($destinataire);

                    $em->persist($message);
                    $em->flush();
                    
                    $request->getSession()->getFlashBag()->add('good_notice', 'Message envoyé.');
                } else
                $request->getSession()->getFlashBag()->add("bad_notice", "L'envoie du message a échoué, le destinataire n'existe pas.");
                
                return $this->redirectToRoute('instagram_messagerie');
            }
        }
 
        $envoyes = $em->getRepository('InstagramBundle:Message')->findBy(array("expediteur" => $this->getUser()), array("id" => "DESC"));
        $recus = $em->getRepository('InstagramBundle:Message')->findBy(array("destinataire" => $this->getUser()), array("id" => "DESC"));
        
        return $this->render("InstagramBundle:Default:messagerie.html.twig", array(
            "form" => $form->createView(),
            "envoyes"   => $envoyes,
            "recus"     => $recus
        ));
    }
    
    //----------AJAX----------//
    
    public function loadActualityAction(Request $request)
    {  
        $datas = null;
        if ($request->isXmlHttpRequest()) {
            $first = $request->request->get("first");
            if ($first != "undefined") {
                $me = $this->getUser();
                
                // on récupère les publications des personnes que l'on suit
                $repository = $this->getDoctrine()
                    ->getManager()
                    ->getRepository('InstagramBundle:Publication')
                ;
                $abonnementsId = [];
                foreach ($me->getAbonnements() as $a) {
                    $abonnementsId[] = $a->getId();
                }
                if (count($abonnementsId) > 0) {
                    $publications = $repository->findBy((array("user" => $abonnementsId)), array("id" => "DESC"), 5, $first);
                } else
                    $publications = null;

                $datas = array(
                    "actuality"  => $this->renderView("InstagramBundle:Default:publications.html.twig", array(
                        "type"          => "home",
                        "publications"  => $publications
                    )),
                    "first"      => $first + 5
                );
            }
        }
                  
        return new JsonResponse($datas);
    }
    
    public function loadcommentsAction(Request $request)
    {
        if ($request->isXmlHttpRequest()) {
            // On crée un objet Commentaire
            $commentaire = new Commentaire();
            $form_commentaire = $this->createForm(CommentaireType::class, $commentaire);
        
            // on récupère la publication
            $repository = $this->getDoctrine()
                ->getManager()
                ->getRepository('InstagramBundle:Publication')
            ;
            $publication = $repository->find($request->request->get("id"));
            
            // on récupère les commentaires de la publication
            $repository = $this->getDoctrine()
                ->getManager()
                ->getRepository('InstagramBundle:Commentaire')
            ;
            $commentaires = $repository->findBy(array("publication" => $publication), array("date" => "DESC"));
            
            $repository = $this->getDoctrine()
                ->getManager()
                ->getRepository('InstagramBundle:Jaime')
            ;
            $nbrJaimes = $repository->nbrJaimes($publication->getId());
            
            $me = $this->getUser();
            if ($me) {
                $jaimeDeja = ($repository->findOneBy(array("publication" => $publication, "user" => $me)) != null) ? true : false;
            } else
                $jaimeDeja = false;
            
            $datas = array(
                "commentaires" => $this->renderView("InstagramBundle:Default:commentaire.html.twig", array(
                    "form"          => $form_commentaire->createView(),
                    "publication"   => $publication,
                    "commentaires"  => $commentaires,
                    "jaimeDeja"     => $jaimeDeja,
                    "nbrJaimes"     => $nbrJaimes
                ))
            );

            return new JsonResponse($datas);
        }
    }
    
    public function loadPublicationsAction(Request $request)
    {  
        $datas = null;
        if ($request->isXmlHttpRequest()) {
            $id_profil = $request->request->get("id");
            $first = $request->request->get("first");
            if ($id_profil != "undefined" && $first != "undefined") {
                // on récupère les publications
                $repository = $this->getDoctrine()
                    ->getManager()
                    ->getRepository('InstagramBundle:Publication')
                ;
                $publications = $repository->findBy((array("user" => $id_profil)), array("id" => "DESC"), 12, $first);

                
                if ($publications) {
                    $datas = array(
                        "publications"  => $this->renderView("InstagramBundle:Default:publications.html.twig", array(
                            "type"          => "profil",
                            "publications"  => $publications
                        )),
                        "first"         => $first + 12
                    );
                }
            }
        }
                  
        return new JsonResponse($datas);
    }
    
    public function searchUserAction(Request $request)
    {  
        $datas = array("suggestions" => "");
        if ($request->isXmlHttpRequest()) {
            $user = $request->request->get("user");
            if ($user != "undefined" && $user != "") {
                // on récupère les publications
                $repository = $this->getDoctrine()
                    ->getManager()
                    ->getRepository('InstagramBundle:User')
                ;
                $users =  $repository->findWithPattern($user);
                if ($users) {
                    $datas = array(
                        "suggestions"  => $this->renderView("InstagramBundle:Default:usersSuggestions.html.twig", array(
                            "users" => $users
                        ))
                    );
                }
            }
        }
                  
        return new JsonResponse($datas);
    }
}
