<?php

namespace InstagramBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Commentaire
 *
 * @ORM\Table(name="commentaire")
 * @ORM\Entity(repositoryClass="InstagramBundle\Repository\CommentaireRepository")
 */
class Commentaire
{
    public function __construct()
  	{
        $this->date = new \Datetime();
  	}
    
    /**
     * @var int
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;
       
    /**
     * @ORM\ManyToOne(targetEntity="InstagramBundle\Entity\Publication")
     * @ORM\JoinColumn(nullable=false)
     */
  	private $publication;
	
	/**
     * @ORM\ManyToOne(targetEntity="InstagramBundle\Entity\User")
     * @ORM\JoinColumn(nullable=false)
     */
  	private $user;
	
    /**
     * @var string
     *
     * @ORM\Column(name="value", type="string", length=255)
     */
  	private $value;
	
	  /**
     * @var \DateTime
     *
     * @ORM\Column(name="date", type="datetime")
     */
    private $date;
		
	
    /**
     * Get id
     *
     * @return integer
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set publication
     *
     * @param \InstagramBundle\Entity\Publication $publication
     *
     * @return Commentaire
     */
    public function setPublication(\InstagramBundle\Entity\Publication $publication)
    {
        $this->publication = $publication;

        return $this;
    }

    /**
     * Get publication
     *
     * @return \InstagramBundle\Entity\Publication
     */
    public function getPublication()
    {
        return $this->publication;
    }

    /**
     * Set value
     *
     * @param string $value
     *
     * @return Commentaire
     */
    public function setValue($value)
    {
        $this->value = $value;

        return $this;
    }

    /**
     * Get value
     *
     * @return string
     */
    public function getValue()
    {
        return $this->value;
    }

    /**
     * Set date
     *
     * @param \DateTime $date
     *
     * @return Commentaire
     */
    public function setDate($date)
    {
        $this->date = $date;

        return $this;
    }

    /**
     * Get date
     *
     * @return \DateTime
     */
    public function getDate()
    {
        return $this->date;
    }

    /**
     * Set user
     *
     * @param \InstagramBundle\Entity\User $user
     *
     * @return Commentaire
     */
    public function setUser(\InstagramBundle\Entity\User $user)
    {
        $this->user = $user;

        return $this;
    }

    /**
     * Get user
     *
     * @return \InstagramBundle\Entity\User
     */
    public function getUser()
    {
        return $this->user;
    }
}
