<?php

namespace InstagramBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Message
 *
 * @ORM\Table(name="message")
 * @ORM\Entity(repositoryClass="InstagramBundle\Repository\MessageRepository")
 */
class Message
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
     * @var string
     *
     * @ORM\Column(name="value", type="string", length=255)
     */
  	private $value;

    /**
   	 * @ORM\ManyToOne(targetEntity="InstagramBundle\Entity\User")
   	 * @ORM\JoinColumn(nullable=false)
   	 */
  	private $expediteur;
	
    /**
   	 * @ORM\ManyToOne(targetEntity="InstagramBundle\Entity\User")
   	 * @ORM\JoinColumn(nullable=false)
   	 */
  	private $destinataire;
    
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
     * Set value
     *
     * @param string $value
     *
     * @return Message
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
     * Set expediteur
     *
     * @param \InstagramBundle\Entity\User $expediteur
     *
     * @return Message
     */
    public function setExpediteur(\InstagramBundle\Entity\User $expediteur)
    {
        $this->expediteur = $expediteur;

        return $this;
    }

    /**
     * Get expediteur
     *
     * @return \InstagramBundle\Entity\User
     */
    public function getExpediteur()
    {
        return $this->expediteur;
    }

    /**
     * Set destinataire
     *
     * @param \InstagramBundle\Entity\User $destinataire
     *
     * @return Message
     */
    public function setDestinataire(\InstagramBundle\Entity\User $destinataire)
    {
        $this->destinataire = $destinataire;

        return $this;
    }

    /**
     * Get destinataire
     *
     * @return \InstagramBundle\Entity\User
     */
    public function getDestinataire()
    {
        return $this->destinataire;
    }

    /**
     * Set date
     *
     * @param \DateTime $date
     *
     * @return Message
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
}
