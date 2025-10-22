package com.hubble.service;

import com.hubble.dto.*;
import com.hubble.entity.*;
import com.hubble.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public void subscribe(Long subscriberId, Long authorId) {
        if (subscriberId.equals(authorId)) {
            throw new IllegalArgumentException("Cannot subscribe to yourself");
        }
        
        User subscriber = userRepository.findById(subscriberId)
            .orElseThrow(() -> new IllegalArgumentException("Subscriber not found"));
        
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        
        if (subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriberId, authorId)) {
            throw new IllegalArgumentException("Already subscribed");
        }
        
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setAuthor(author);
        subscription.setNotificationEnabled(true);
        
        subscriptionRepository.save(subscription);
    }

    public void unsubscribe(Long subscriberId, Long authorId) {
        if (!subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriberId, authorId)) {
            throw new IllegalArgumentException("Not subscribed");
        }
        
        subscriptionRepository.deleteBySubscriberIdAndAuthorId(subscriberId, authorId);
    }

    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getSubscriptionStatus(Long subscriberId, Long authorId) {
        SubscriptionStatusResponse response = new SubscriptionStatusResponse();
        
        subscriptionRepository.findBySubscriberIdAndAuthorId(subscriberId, authorId)
            .ifPresentOrElse(
                subscription -> {
                    response.setIsSubscribed(true);
                    response.setNotificationEnabled(subscription.getNotificationEnabled());
                },
                () -> {
                    response.setIsSubscribed(false);
                    response.setNotificationEnabled(false);
                }
            );
        
        return response;
    }

    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getMySubscriptions(Long userId, Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findBySubscriberIdOrderByCreatedAtDesc(userId, pageable);
        return subscriptions.map(this::toSubscriptionResponse);
    }

    @Transactional(readOnly = true)
    public Page<SubscriberResponse> getMySubscribers(Long userId, Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable);
        return subscriptions.map(this::toSubscriberResponse);
    }

    private SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        response.setAuthorId(subscription.getAuthor().getId());
        response.setAuthorNickname(subscription.getAuthor().getNickname());
        response.setNotificationEnabled(subscription.getNotificationEnabled());
        response.setCreatedAt(subscription.getCreatedAt());
        
        userProfileRepository.findByUserId(subscription.getAuthor().getId())
            .ifPresent(profile -> response.setAuthorAvatarUrl(profile.getAvatarUrl()));
        
        return response;
    }
    
    private SubscriberResponse toSubscriberResponse(Subscription subscription) {
        SubscriberResponse response = new SubscriberResponse();
        response.setId(subscription.getId());
        response.setSubscriberId(subscription.getSubscriber().getId());
        response.setSubscriberNickname(subscription.getSubscriber().getNickname());
        response.setCreatedAt(subscription.getCreatedAt());
        
        userProfileRepository.findByUserId(subscription.getSubscriber().getId())
            .ifPresent(profile -> response.setSubscriberAvatarUrl(profile.getAvatarUrl()));
        
        return response;
    }
}
