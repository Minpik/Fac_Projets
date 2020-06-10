<?php

namespace InstagramBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Jaime
 *
 * @ORM\Table(name="jaime")
 * @ORM\Entity(repositoryClass="InstagramBundle\Repository\JaimeRepository")
 */
class Jaime
{
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
     * @return Jaime
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
     * Set user
     *
     * @param \InstagramBundle\Entity\User $user
     *
     * @return Jaime
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
