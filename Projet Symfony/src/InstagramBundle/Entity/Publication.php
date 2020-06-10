<?php

namespace InstagramBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Publication
 *
 * @ORM\Table(name="publication")
 * @ORM\Entity(repositoryClass="InstagramBundle\Repository\PublicationRepository")
 */
class Publication
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
     * @ORM\ManyToOne(targetEntity="InstagramBundle\Entity\User")
     * @ORM\JoinColumn(nullable=false)
     */
  	private $user;

	
	  /**
   	 * @ORM\Column(name="image", type="string", length=255)
   	 */
  	private $image;

	  /**
   	 * @ORM\Column(name="description", type="string", length=255)
   	 */
    private $description;
	
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
     * Set image
     *
     * @param string $image
     *
     * @return Publication
     */
    public function setImage($image)
    {
        $this->image = $image;

        return $this;
    }

    /**
     * Get image
     *
     * @return string
     */
    public function getImage()
    {
        return $this->image;
    }

    /**
     * Set description
     *
     * @param string $description
     *
     * @return Publication
     */
    public function setDescription($description)
    {
        $this->description = $description;

        return $this;
    }

    /**
     * Get description
     *
     * @return string
     */
    public function getDescription()
    {
        return $this->description;
    }

    /**
     * Set date
     *
     * @param \DateTime $date
     *
     * @return Publication
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
     * @return Publication
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
