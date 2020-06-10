<?php

namespace InstagramBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\Extension\Core\Type\FileType;

class PublicationType extends AbstractType
{
    /**
     * {@inheritdoc}
     */
	  public function buildForm(FormBuilderInterface $builder, array $options)
    {
    		$builder
            ->add("description",    TextType::class)
            ->add('image',          FileType::class)
						->add("publier",        SubmitType::class)
        ;
    }
	
		/**
     * {@inheritdoc}
     */
    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'InstagramBundle\Entity\Publication'
        ));
    }

    /**
     * {@inheritdoc}
     */
    public function getBlockPrefix()
    {
        return 'instagrambundle_publication';
    }


}
